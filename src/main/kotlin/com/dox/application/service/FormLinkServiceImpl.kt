package com.dox.application.service

import com.dox.adapter.out.tenant.TenantContext
import com.dox.application.port.input.CreateFormLinkCommand
import com.dox.application.port.input.CreateFormResponseCommand
import com.dox.application.port.input.FormLinkUseCase
import com.dox.application.port.input.FormLinkWithToken
import com.dox.application.port.input.FormUseCase
import com.dox.application.port.input.PublicFormData
import com.dox.application.port.input.PublicFormSubmitCommand
import com.dox.application.port.output.AuthTokenPort
import com.dox.application.port.output.CustomerPersistencePort
import com.dox.application.port.output.FormLinkPersistencePort
import com.dox.domain.enum.FormLinkStatus
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.FormLink
import com.dox.domain.model.FormResponse
import com.dox.shared.ContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class FormLinkServiceImpl(
    private val formLinkPersistencePort: FormLinkPersistencePort,
    private val formUseCase: FormUseCase,
    private val customerPersistencePort: CustomerPersistencePort,
    private val authTokenPort: AuthTokenPort
) : FormLinkUseCase {

    @Transactional
    override fun createFormLink(command: CreateFormLinkCommand): FormLinkWithToken {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        val userId = ContextHolder.getUserIdOrThrow()

        formUseCase.findFormById(command.formId)
        customerPersistencePort.findById(command.customerId)
            ?: throw ResourceNotFoundException("Cliente", command.customerId.toString())

        val expiresAt = LocalDateTime.now().plusHours(command.expiresInHours)

        val formLink = formLinkPersistencePort.save(
            FormLink(
                formId = command.formId,
                customerId = command.customerId,
                createdBy = userId,
                expiresAt = expiresAt
            )
        )

        val token = authTokenPort.generateFormLinkToken(tenantId, formLink.id, expiresAt)
        return FormLinkWithToken(formLink, token)
    }

    override fun findFormLinksByTenant(): List<FormLinkWithToken> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        return formLinkPersistencePort.findAll().map { formLink ->
            val token = authTokenPort.generateFormLinkToken(tenantId, formLink.id, formLink.expiresAt)
            FormLinkWithToken(formLink, token)
        }
    }

    @Transactional
    override fun revokeFormLink(id: UUID) {
        val formLink = formLinkPersistencePort.findById(id)
            ?: throw ResourceNotFoundException("FormLink", id.toString())
        formLinkPersistencePort.save(formLink.copy(status = FormLinkStatus.EXPIRED))
    }

    override fun resolvePublicForm(token: String): PublicFormData {
        val tokenData = extractAndValidateToken(token)

        return TenantContext.withTenantContext(tokenData.tenantId) {
            val formLink = findAndValidateFormLink(tokenData.formLinkId)
            val form = formUseCase.findFormById(formLink.formId)
            val customer = customerPersistencePort.findById(formLink.customerId)
            val customerName = customer?.data?.get("name") as? String

            PublicFormData(
                formTitle = form.title,
                formDescription = form.description,
                fields = form.fields,
                customerName = customerName,
                expiresAt = formLink.expiresAt
            )
        }
    }

    @Transactional
    override fun submitPublicForm(command: PublicFormSubmitCommand): FormResponse {
        val tokenData = extractAndValidateToken(command.token)

        return TenantContext.withTenantContext(tokenData.tenantId) {
            val formLink = findAndValidateFormLink(tokenData.formLinkId)
            val customer = customerPersistencePort.findById(formLink.customerId)
            val customerName = customer?.data?.get("name") as? String

            val response = formUseCase.createResponse(
                CreateFormResponseCommand(
                    formId = formLink.formId,
                    customerId = formLink.customerId,
                    customerName = customerName,
                    answers = command.answers
                )
            )

            formLinkPersistencePort.save(formLink.copy(status = FormLinkStatus.ANSWERED))
            response
        }
    }

    private fun extractAndValidateToken(token: String): com.dox.application.port.output.FormLinkTokenData {
        return try {
            authTokenPort.extractFormLinkData(token)
        } catch (_: Exception) {
            throw BusinessException("Link inválido ou expirado")
        }
    }

    private fun findAndValidateFormLink(formLinkId: UUID): FormLink {
        val formLink = formLinkPersistencePort.findById(formLinkId)
            ?: throw ResourceNotFoundException("FormLink", formLinkId.toString())

        if (formLink.status == FormLinkStatus.ANSWERED) {
            throw BusinessException("Este link já foi utilizado")
        }

        if (formLink.isExpired()) {
            formLinkPersistencePort.save(formLink.copy(status = FormLinkStatus.EXPIRED))
            throw BusinessException("Este link expirou")
        }

        if (formLink.status == FormLinkStatus.EXPIRED) {
            throw BusinessException("Este link expirou")
        }

        return formLink
    }

}
