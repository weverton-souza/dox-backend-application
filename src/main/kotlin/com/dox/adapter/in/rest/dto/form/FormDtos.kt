package com.dox.adapter.`in`.rest.dto.form

import com.dox.domain.enum.FormResponseStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

data class FormRequest(
    @field:NotBlank(message = "Título é obrigatório")
    @field:Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    val title: String,
    @field:Size(max = 2000, message = "Descrição deve ter no máximo 2000 caracteres")
    val description: String? = null,
    @field:Size(max = 200, message = "Máximo de 200 campos permitidos")
    val fields: List<Map<String, Any?>> = emptyList(),
    val linkedTemplateId: UUID? = null,
    @field:Size(max = 200, message = "Máximo de 200 mapeamentos permitidos")
    val fieldMappings: List<Map<String, Any?>> = emptyList(),
)

data class FormResponseDto(
    val id: UUID,
    val title: String,
    val description: String?,
    val fields: List<Map<String, Any?>>,
    val linkedTemplateId: UUID?,
    val fieldMappings: List<Map<String, Any?>>,
    val currentVersion: Int,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)

data class FormVersionResponseDto(
    val id: UUID,
    val formId: UUID,
    val version: Int,
    val title: String,
    val description: String?,
    val fields: List<Map<String, Any?>>,
    val fieldMappings: List<Map<String, Any?>>,
    val createdAt: LocalDateTime?,
)

data class FormResponseRequest(
    val customerId: UUID? = null,
    @field:Size(max = 255, message = "Nome do cliente deve ter no máximo 255 caracteres")
    val customerName: String? = null,
    val status: FormResponseStatus? = null,
    @field:Size(max = 200, message = "Máximo de 200 respostas permitidas")
    val answers: List<Map<String, Any?>> = emptyList(),
)

data class FormResponseResponseDto(
    val id: UUID,
    val formId: UUID,
    val formVersionId: UUID,
    val customerId: UUID?,
    val customerName: String?,
    val status: FormResponseStatus,
    val answers: List<Map<String, Any?>>,
    val generatedReportId: UUID?,
    val version: Int?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)
