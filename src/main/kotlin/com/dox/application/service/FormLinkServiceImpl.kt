package com.dox.application.service

import com.dox.adapter.out.tenant.TenantContext
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
import com.dox.application.port.output.AuthTokenPort
import com.dox.application.port.output.CustomerPersistencePort
import com.dox.application.port.output.FormLinkPersistencePort
import com.dox.domain.enum.FormLinkStatus
import com.dox.domain.enum.RespondentType
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.CustomerContact
import com.dox.domain.model.FormLink
import com.dox.domain.model.FormResponse
import com.dox.shared.ContextHolder
import io.jsonwebtoken.JwtException
import org.slf4j.LoggerFactory
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

        return command.recipients.map { recipient ->
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
            FormLinkWithToken(
                formLink = link,
                token = token,
                respondent = buildRespondentInfo(recipient.respondentType, customer.displayName(), contact),
            )
        }
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
        return formLinkPersistencePort.findAll().map { it.toWithToken(tenantId) }
    }

    override fun findFormLinksByCustomer(customerId: UUID): List<FormLinkWithToken> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        return formLinkPersistencePort.findByCustomerId(customerId).map { it.toWithToken(tenantId) }
    }

    private fun FormLink.toWithToken(tenantId: UUID): FormLinkWithToken {
        val token = authTokenPort.generateFormLinkToken(tenantId, id, expiresAt)
        val customerName = customerPersistencePort.findById(customerId)?.displayName()
        val contact = customerContactId?.let { customerPersistencePort.findContactById(it) }
        return FormLinkWithToken(
            formLink = this,
            token = token,
            respondent = buildRespondentInfo(respondentType, customerName, contact),
        )
    }

    @Transactional
    override fun revokeFormLink(id: UUID) {
        val formLink =
            formLinkPersistencePort.findById(id)
                ?: throw ResourceNotFoundException("FormLink", id.toString())
        formLinkPersistencePort.save(formLink.copy(status = FormLinkStatus.EXPIRED))
    }

    override fun resolvePublicForm(token: String): PublicFormData {
        val tokenData = extractAndValidateToken(token)

        return TenantContext.withTenantContext(tokenData.tenantId) {
            val formLink = findAndValidateFormLink(tokenData.formLinkId)
            val formWithVersion = formUseCase.findFormById(formLink.formId)
            val customer = customerPersistencePort.findById(formLink.customerId)
            val contact = formLink.customerContactId?.let { customerPersistencePort.findContactById(it) }
            val respondentName = resolveRespondentName(formLink.respondentType, customer?.displayName(), contact)

            PublicFormData(
                formTitle = formWithVersion.version.title,
                formDescription = formWithVersion.version.description,
                fields = formWithVersion.version.fields,
                customerName = customer?.displayName(),
                respondentName = respondentName,
                respondentType = formLink.respondentType,
                expiresAt = formLink.expiresAt,
            )
        }
    }

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
                    ),
                )

            formLinkPersistencePort.save(formLink.copy(status = FormLinkStatus.ANSWERED))
            response
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
