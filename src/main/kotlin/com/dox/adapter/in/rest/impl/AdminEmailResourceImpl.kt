package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.email.AdminEmailTestRequest
import com.dox.adapter.`in`.rest.dto.email.AdminPagedEmailLogResponse
import com.dox.adapter.`in`.rest.dto.email.EmailLogResponse
import com.dox.adapter.`in`.rest.resource.admin.AdminEmailResource
import com.dox.application.port.input.EmailUseCase
import com.dox.application.port.input.TestEmailCommand
import com.dox.domain.email.EmailLog
import com.dox.domain.email.EmailLogStatus
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class AdminEmailResourceImpl(
    private val emailUseCase: EmailUseCase,
) : AdminEmailResource {
    override fun sendTest(request: AdminEmailTestRequest): ResponseEntity<EmailLogResponse> {
        val log =
            emailUseCase.sendTest(
                TestEmailCommand(
                    templateId = request.templateId,
                    recipient = request.recipient,
                    sampleVariables = request.sampleVariables,
                ),
            )
        return responseEntity(log.toResponse())
    }

    override fun listLog(
        templateId: String?,
        status: EmailLogStatus?,
        recipientEmail: String?,
        tenantId: UUID?,
        pageNumber: Int,
        pageSize: Int,
    ): ResponseEntity<AdminPagedEmailLogResponse> {
        val pageable = PageRequest.of(pageNumber.coerceAtLeast(0), pageSize.coerceIn(1, 100))
        val page = emailUseCase.listLogs(templateId, status, recipientEmail, tenantId, pageable)
        return responseEntity(
            AdminPagedEmailLogResponse(
                content = page.content.map { it.toResponse() },
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                pageNumber = page.number,
                pageSize = page.size,
            ),
        )
    }

    private fun EmailLog.toResponse() =
        EmailLogResponse(
            id = id,
            tenantId = tenantId,
            templateId = templateId,
            recipientEmail = recipientEmail,
            subject = subject,
            providerId = providerId,
            status = status,
            errorMessage = errorMessage,
            idempotencyKey = idempotencyKey,
            tags = tags,
            sentAt = sentAt,
            updatedAt = updatedAt,
        )
}
