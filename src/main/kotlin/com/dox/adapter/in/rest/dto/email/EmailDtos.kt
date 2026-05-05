package com.dox.adapter.`in`.rest.dto.email

import com.dox.domain.email.EmailLogStatus
import com.dox.domain.email.EmailTemplateId
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.UUID

data class AdminEmailTestRequest(
    @field:NotNull(message = "Template é obrigatório")
    val templateId: EmailTemplateId,
    @field:NotBlank(message = "Destinatário é obrigatório")
    @field:Email(message = "Email inválido")
    val recipient: String,
    val sampleVariables: Map<String, Any?> = emptyMap(),
)

data class EmailLogResponse(
    val id: UUID,
    val tenantId: UUID?,
    val templateId: String,
    val recipientEmail: String,
    val subject: String,
    val providerId: String?,
    val status: EmailLogStatus,
    val errorMessage: String?,
    val idempotencyKey: String?,
    val tags: Map<String, String>,
    val sentAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class AdminPagedEmailLogResponse(
    val content: List<EmailLogResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val pageNumber: Int,
    val pageSize: Int,
)
