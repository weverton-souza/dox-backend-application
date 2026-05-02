package com.dox.adapter.`in`.rest.dto.formlink

import com.dox.domain.enum.FormLinkStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

data class CreateFormLinkRequest(
    @field:NotNull(message = "ID do formulário é obrigatório")
    val formId: UUID,
    @field:NotNull(message = "ID do cliente é obrigatório")
    val customerId: UUID,
    @field:Min(value = 1, message = "Expiração deve ser de pelo menos 1 hora")
    val expiresInHours: Long = 72,
)

data class FormLinkResponse(
    val id: UUID,
    val token: String,
    val formId: UUID,
    val customerId: UUID,
    val status: FormLinkStatus,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)

data class PublicFormResponse(
    val formTitle: String,
    val formDescription: String?,
    val fields: List<Map<String, Any?>>,
    val customerName: String?,
    val expiresAt: LocalDateTime,
)

data class PublicFormSubmitRequest(
    @field:Size(max = 200, message = "Máximo de 200 respostas permitidas")
    val answers: List<Map<String, Any?>> = emptyList(),
)

data class PublicFormSubmitResponse(
    val message: String = "Resposta enviada com sucesso",
)

data class PublicFormDraftRequest(
    val partialResponse: Map<String, Any?> = emptyMap(),
)

data class PublicFormDraftResponse(
    val partialResponse: Map<String, Any?>,
    val savedAt: LocalDateTime,
)
