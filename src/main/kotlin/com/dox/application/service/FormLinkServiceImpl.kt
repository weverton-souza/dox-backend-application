package com.dox.application.service

import com.dox.application.port.input.CreateFormLinkCommand
import com.dox.application.port.input.CreateFormResponseCommand
import com.dox.application.port.input.FormLinkUseCase
import com.dox.application.port.input.FormLinkWithToken
import com.dox.application.port.input.FormUseCase
import com.dox.application.port.input.MultiSendCommand
import com.dox.application.port.input.PublicFormData
import com.dox.application.port.input.PublicFormSubmitCommand
import com.dox.application.port.input.RecipientSpec
import com.dox.application.port.input.RespondentInfo
import com.dox.application.port.input.SavePublicDraftCommand
import com.dox.application.port.output.AuthTokenPort
import com.dox.application.port.output.CustomerPersistencePort
import com.dox.application.port.output.FormDraftPersistencePort
import com.dox.application.port.output.FormLinkFollowupPersistencePort
import com.dox.application.port.output.FormLinkPersistencePort
import com.dox.application.port.output.ProfessionalSettingsPersistencePort
import com.dox.domain.email.FollowupSchedule
import com.dox.domain.enum.FormLinkStatus
import com.dox.domain.enum.RespondentType
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.Customer
import com.dox.domain.model.CustomerContact
import com.dox.domain.model.FormDraft
import com.dox.domain.model.FormLink
import com.dox.domain.model.FormLinkFollowup
import com.dox.domain.model.FormResponse
import com.dox.shared.ContextHolder
import com.dox.shared.TenantContext
import io.jsonwebtoken.JwtException
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class FormLinkServiceImpl(
    private val formLinkPersistencePort: FormLinkPersistencePort,
    private val formUseCase: FormUseCase,
    private val customerPersistencePort: CustomerPersistencePort,
    private val authTokenPort: AuthTokenPort,
    private val formDraftPersistencePort: FormDraftPersistencePort,
    private val professionalSettingsPersistencePort: ProfessionalSettingsPersistencePort,
    private val formLinkFollowupPersistencePort: FormLinkFollowupPersistencePort,
    private val eventPublisher: ApplicationEventPublisher,
) : FormLinkUseCase {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun createFormLink(command: CreateFormLinkCommand): FormLinkWithToken {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        val userId = ContextHolder.getUserIdOrThrow()

        val formWithVersion = formUseCase.findFormById(command.formId)
        val customer =
            customerPersistencePort.findById(command.customerId)
                ?: throw ResourceNotFoundException("Cliente", command.customerId.toString())

        val expiresAt = LocalDateTime.now().plusHours(command.expiresInHours)

        val formLink =
            formLinkPersistencePort.save(
                FormLink(
                    formId = command.formId,
                    formVersionId = formWithVersion.version.id,
                    customerId = command.customerId,
                    customerContactId = null,
                    respondentType = RespondentType.CUSTOMER,
                    createdBy = userId,
                    expiresAt = expiresAt,
                ),
            )

        val token = authTokenPort.generateFormLinkToken(tenantId, formLink.id, expiresAt)
        return FormLinkWithToken(
            formLink = formLink,
            token = token,
            respondent =
                RespondentInfo(
                    type = RespondentType.CUSTOMER,
                    name = customer.displayName(),
                ),
        )
    }

    @Transactional
    override fun multiSend(command: MultiSendCommand): List<FormLinkWithToken> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        val userId = ContextHolder.getUserIdOrThrow()

        if (command.recipients.isEmpty()) {
            throw BusinessException("Selecione ao menos um respondente")
        }
        if (command.recipients.size > MAX_RECIPIENTS) {
            throw BusinessException("Máximo de $MAX_RECIPIENTS respondentes por envio")
        }

        val formWithVersion = formUseCase.findFormById(command.formId)
        val customer =
            customerPersistencePort.findById(command.customerId)
                ?: throw ResourceNotFoundException("Cliente", command.customerId.toString())

        val contactIds = command.recipients.mapNotNull { it.customerContactId }.toSet()
        val contactsById =
            contactIds.associateWith { id ->
                val contact =
                    customerPersistencePort.findContactById(id)
                        ?: throw ResourceNotFoundException("Contato", id.toString())
                if (contact.customerId != command.customerId) {
                    throw BusinessException("Contato $id não pertence ao cliente informado")
                }
                if (!contact.canReceiveForms) {
                    throw BusinessException("Contato ${contact.name} não pode receber formulários")
                }
                contact
            }

        val expiresAt = LocalDateTime.now().plusHours(command.expiresInHours)

        val professional =
            if (command.sendEmail) {
                professionalSettingsPersistencePort.find()
            } else {
                null
            }

        val results =
            command.recipients.map { recipient ->
                validateRecipient(recipient)
                val contact = recipient.customerContactId?.let { contactsById[it] }
                val link =
                    formLinkPersistencePort.save(
                        FormLink(
                            formId = command.formId,
                            formVersionId = formWithVersion.version.id,
                            customerId = command.customerId,
                            customerContactId = recipient.customerContactId,
                            respondentType = recipient.respondentType,
                            createdBy = userId,
                            expiresAt = expiresAt,
                        ),
                    )
                val token = authTokenPort.generateFormLinkToken(tenantId, link.id, expiresAt)

                if (command.sendEmail && professional != null) {
                    publishFormInviteEvent(
                        tenantId = tenantId,
                        link = link,
                        token = token,
                        formTitle = formWithVersion.version.title,
                        customer = customer,
                        contact = contact,
                        professionalName = professional.name.ifBlank { "Profissional" },
                        professionalCouncil = professional.formattedCouncil().ifBlank { null },
                    )
                    scheduleFollowups(link, command.expiresInHours)
                }

                FormLinkWithToken(
                    formLink = link,
                    token = token,
                    respondent = buildRespondentInfo(recipient.respondentType, customer.displayName(), contact),
                )
            }

        return results
    }

    private fun publishFormInviteEvent(
        tenantId: UUID,
        link: FormLink,
        token: String,
        formTitle: String,
        customer: Customer,
        contact: CustomerContact?,
        professionalName: String,
        professionalCouncil: String?,
    ) {
        val recipient = resolveRecipientEmail(link, customer, contact) ?: return
        val isAboutCustomer = link.respondentType == RespondentType.CONTACT
        val respondentName =
            when (link.respondentType) {
                RespondentType.CUSTOMER -> customer.displayName() ?: "cliente"
                RespondentType.CONTACT -> contact?.name ?: "responsável"
                RespondentType.PROFESSIONAL -> return
            }

        eventPublisher.publishEvent(
            FormInviteEmailRequestedEvent(
                tenantId = tenantId,
                formLinkId = link.id,
                recipient = recipient,
                respondentName = respondentName,
                isAboutCustomer = isAboutCustomer,
                customerName = customer.displayName(),
                professionalName = professionalName,
                professionalCouncil = professionalCouncil,
                formTitle = formTitle,
                formToken = token,
                expiresAt = link.expiresAt,
            ),
        )
    }

    private fun resolveRecipientEmail(
        link: FormLink,
        customer: Customer,
        contact: CustomerContact?,
    ): String? =
        when (link.respondentType) {
            RespondentType.CUSTOMER -> (customer.data["email"] as? String)?.trim()?.ifBlank { null }
            RespondentType.CONTACT -> contact?.email?.trim()?.ifBlank { null }
            RespondentType.PROFESSIONAL -> null
        }

    private fun scheduleFollowups(
        link: FormLink,
        ttlHours: Long,
    ) {
        val baseTime = link.createdAt ?: LocalDateTime.now()
        val steps = FollowupSchedule.forTtlHours(ttlHours)
        if (steps.isEmpty()) return

        val followups =
            steps.map { step ->
                FormLinkFollowup(
                    formLinkId = link.id,
                    level = step.level,
                    dayOffset = step.dayOffset,
                    scheduledFor = baseTime.plusDays(step.dayOffset.toLong()),
                )
            }
        formLinkFollowupPersistencePort.saveAll(followups)
    }

    private fun validateRecipient(recipient: RecipientSpec) {
        when (recipient.respondentType) {
            RespondentType.CONTACT ->
                if (recipient.customerContactId == null) {
                    throw BusinessException("Respondente do tipo CONTACT exige customerContactId")
                }
            RespondentType.CUSTOMER, RespondentType.PROFESSIONAL ->
                if (recipient.customerContactId != null) {
                    throw BusinessException("Respondente do tipo ${recipient.respondentType} não aceita customerContactId")
                }
        }
    }

    private fun buildRespondentInfo(
        type: RespondentType,
        customerName: String?,
        contact: CustomerContact?,
    ): RespondentInfo =
        when (type) {
            RespondentType.CUSTOMER ->
                RespondentInfo(type = type, name = customerName)
            RespondentType.CONTACT ->
                RespondentInfo(
                    type = type,
                    name = contact?.name,
                    customerContactId = contact?.id,
                    relationType = contact?.relationType?.name?.lowercase(),
                )
            RespondentType.PROFESSIONAL ->
                RespondentInfo(type = type, name = null)
        }

    override fun findFormLinksByTenant(): List<FormLinkWithToken> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        val links = formLinkPersistencePort.findAll()
        return enrichLinksWithToken(links, tenantId)
    }

    override fun findFormLinksByCustomer(customerId: UUID): List<FormLinkWithToken> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        val links = formLinkPersistencePort.findByCustomerId(customerId)
        return enrichLinksWithToken(links, tenantId)
    }

    private fun enrichLinksWithToken(
        links: List<FormLink>,
        tenantId: UUID,
    ): List<FormLinkWithToken> {
        if (links.isEmpty()) return emptyList()
        val customerIds = links.map { it.customerId }.toSet()
        val contactIds = links.mapNotNull { it.customerContactId }.toSet()
        val customersById =
            customerPersistencePort.findByIds(customerIds).associateBy { it.id }
        val contactsById =
            customerPersistencePort.findContactsByIds(contactIds).associateBy { it.id }

        return links.map { link ->
            val token = authTokenPort.generateFormLinkToken(tenantId, link.id, link.expiresAt)
            val customerName = customersById[link.customerId]?.displayName()
            val contact = link.customerContactId?.let { contactsById[it] }
            FormLinkWithToken(
                formLink = link,
                token = token,
                respondent = buildRespondentInfo(link.respondentType, customerName, contact),
            )
        }
    }

    @Transactional
    override fun revokeFormLink(id: UUID) {
        val formLink =
            formLinkPersistencePort.findById(id)
                ?: throw ResourceNotFoundException("FormLink", id.toString())
        formLinkPersistencePort.save(formLink.copy(status = FormLinkStatus.EXPIRED))
    }

    @Transactional
    override fun resendInvite(id: UUID): FormLinkWithToken {
        val tenantId = ContextHolder.getTenantIdOrThrow()

        val link =
            formLinkPersistencePort.findById(id)
                ?: throw ResourceNotFoundException("FormLink", id.toString())

        if (link.status != FormLinkStatus.PENDING) {
            throw BusinessException("Apenas links pendentes podem ser reenviados")
        }
        if (link.isExpired()) {
            throw BusinessException("Este link expirou — gere um novo")
        }
        if (link.manualResendCount >= FormLink.MAX_MANUAL_RESENDS) {
            throw BusinessException("Limite de ${FormLink.MAX_MANUAL_RESENDS} reenvios manuais atingido")
        }

        val customer =
            customerPersistencePort.findById(link.customerId)
                ?: throw ResourceNotFoundException("Cliente", link.customerId.toString())
        val contact = link.customerContactId?.let { customerPersistencePort.findContactById(it) }

        val recipient =
            resolveRecipientEmail(link, customer, contact)
                ?: throw BusinessException("Respondente sem email cadastrado")

        val professional =
            professionalSettingsPersistencePort.find()
                ?: throw BusinessException("Profissional sem dados cadastrados")
        val formWithVersion = formUseCase.findFormById(link.formId)
        val token = authTokenPort.generateFormLinkToken(tenantId, link.id, link.expiresAt)

        val respondentName =
            when (link.respondentType) {
                RespondentType.CUSTOMER -> customer.displayName() ?: "cliente"
                RespondentType.CONTACT -> contact?.name ?: "responsável"
                RespondentType.PROFESSIONAL -> throw BusinessException("Reenvio não suportado para profissional")
            }

        eventPublisher.publishEvent(
            FormInviteEmailRequestedEvent(
                tenantId = tenantId,
                formLinkId = link.id,
                recipient = recipient,
                respondentName = respondentName,
                isAboutCustomer = link.respondentType == RespondentType.CONTACT,
                customerName = customer.displayName(),
                professionalName = professional.name.ifBlank { "Profissional" },
                professionalCouncil = professional.formattedCouncil().ifBlank { null },
                formTitle = formWithVersion.version.title,
                formToken = token,
                expiresAt = link.expiresAt,
            ),
        )

        val updated =
            formLinkPersistencePort.save(
                link.copy(manualResendCount = link.manualResendCount + 1),
            )

        return FormLinkWithToken(
            formLink = updated,
            token = token,
            respondent = buildRespondentInfo(link.respondentType, customer.displayName(), contact),
        )
    }

    override fun resolvePublicForm(token: String): PublicFormData {
        val tokenData = extractAndValidateToken(token)

        return TenantContext.withTenantContext(tokenData.tenantId) {
            val formLink = findAndValidateFormLink(tokenData.formLinkId)
            val formWithVersion = formUseCase.findFormById(formLink.formId)
            val customer = customerPersistencePort.findById(formLink.customerId)
            val contact = formLink.customerContactId?.let { customerPersistencePort.findContactById(it) }
            val respondentName = resolveRespondentName(formLink.respondentType, customer?.displayName(), contact)

            if (formLink.firstViewedAt == null) {
                formLinkPersistencePort.save(formLink.copy(firstViewedAt = LocalDateTime.now()))
            }

            PublicFormData(
                formTitle = formWithVersion.version.title,
                formDescription = formWithVersion.version.description,
                fields = filterOutPresencialFields(formWithVersion.version.fields),
                customerName = customer?.displayName(),
                respondentName = respondentName,
                respondentType = formLink.respondentType,
                expiresAt = formLink.expiresAt,
            )
        }
    }

    private fun filterOutPresencialFields(fields: List<Map<String, Any?>>): List<Map<String, Any?>> = fields.filter { (it["collectionMode"] as? String) != "presencial" }

    override fun submitPublicForm(command: PublicFormSubmitCommand): FormResponse {
        val tokenData = extractAndValidateToken(command.token)

        return TenantContext.withTenantContext(tokenData.tenantId) {
            val formLink = findAndValidateFormLink(tokenData.formLinkId)
            val customer = customerPersistencePort.findById(formLink.customerId)
            val contact = formLink.customerContactId?.let { customerPersistencePort.findContactById(it) }
            val respondentName = resolveRespondentName(formLink.respondentType, customer?.displayName(), contact)

            val response =
                formUseCase.createResponse(
                    CreateFormResponseCommand(
                        formId = formLink.formId,
                        formVersionId = formLink.formVersionId,
                        customerId = formLink.customerId,
                        customerName = customer?.displayName(),
                        customerContactId = formLink.customerContactId,
                        respondentType = formLink.respondentType,
                        respondentName = respondentName,
                        answers = command.answers,
                        pageDurationsMs = command.pageDurationsMs,
                    ),
                )

            formLinkPersistencePort.save(formLink.copy(status = FormLinkStatus.ANSWERED))
            formDraftPersistencePort.deleteByFormLinkId(formLink.id)
            response
        }
    }

    override fun getPublicDraft(token: String): FormDraft? {
        val tokenData = extractAndValidateToken(token)
        return TenantContext.withTenantContext(tokenData.tenantId) {
            findAndValidateFormLink(tokenData.formLinkId)
            formDraftPersistencePort.findByFormLinkId(tokenData.formLinkId)
        }
    }

    @Transactional
    override fun savePublicDraft(command: SavePublicDraftCommand): FormDraft {
        val tokenData = extractAndValidateToken(command.token)
        return TenantContext.withTenantContext(tokenData.tenantId) {
            findAndValidateFormLink(tokenData.formLinkId)
            formDraftPersistencePort.save(
                FormDraft(
                    formLinkId = tokenData.formLinkId,
                    partialResponse = command.partialResponse,
                ),
            )
        }
    }

    private fun resolveRespondentName(
        type: RespondentType,
        customerName: String?,
        contact: CustomerContact?,
    ): String? =
        when (type) {
            RespondentType.CUSTOMER -> customerName
            RespondentType.CONTACT -> contact?.name
            RespondentType.PROFESSIONAL -> null
        }

    private fun extractAndValidateToken(token: String): com.dox.application.port.output.FormLinkTokenData {
        return try {
            authTokenPort.extractFormLinkData(token)
        } catch (e: JwtException) {
            log.warn("Invalid form link token: {}", e.message)
            throw BusinessException("Link inválido ou expirado")
        } catch (e: IllegalArgumentException) {
            log.warn("Malformed form link token: {}", e.message)
            throw BusinessException("Link inválido ou expirado")
        }
    }

    private fun findAndValidateFormLink(formLinkId: UUID): FormLink {
        val formLink =
            formLinkPersistencePort.findById(formLinkId)
                ?: throw ResourceNotFoundException("FormLink", formLinkId.toString())

        if (formLink.status == FormLinkStatus.ANSWERED) {
            throw BusinessException("Este link já foi utilizado")
        }

        if (formLink.status == FormLinkStatus.EXPIRED) {
            throw BusinessException("Este link expirou")
        }

        if (formLink.isExpired()) {
            formLinkPersistencePort.save(formLink.copy(status = FormLinkStatus.EXPIRED))
            throw BusinessException("Este link expirou")
        }

        return formLink
    }

    companion object {
        private const val MAX_RECIPIENTS = 20
    }
}
