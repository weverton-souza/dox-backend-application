package com.dox.adapter.`in`.rest.dto.formlink

import com.dox.domain.enum.FormLinkStatus
import java.time.LocalDateTime
import java.util.UUID

data class CreateFormLinkRequest(
    val formId: UUID,
    val customerId: UUID,
    val expiresInHours: Long = 72
)

data class FormLinkResponse(
    val id: UUID,
    val token: String,
    val formId: UUID,
    val customerId: UUID,
    val status: FormLinkStatus,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class PublicFormResponse(
    val formTitle: String,
    val formDescription: String?,
    val fields: List<Map<String, Any?>>,
    val customerName: String?,
    val expiresAt: LocalDateTime
)

data class PublicFormSubmitRequest(
    val answers: List<Map<String, Any?>> = emptyList()
)

data class PublicFormSubmitResponse(
    val message: String = "Resposta enviada com sucesso"
)
