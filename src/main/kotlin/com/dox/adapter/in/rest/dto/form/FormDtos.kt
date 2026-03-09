package com.dox.adapter.`in`.rest.dto.form

import com.dox.domain.enum.FormResponseStatus
import java.time.LocalDateTime
import java.util.UUID

data class FormRequest(
    val title: String,
    val description: String? = null,
    val fields: List<Map<String, Any?>> = emptyList(),
    val linkedTemplateId: UUID? = null,
    val fieldMappings: Map<String, Any?> = emptyMap()
)

data class FormResponseDto(
    val id: UUID,
    val title: String,
    val description: String?,
    val fields: List<Map<String, Any?>>,
    val linkedTemplateId: UUID?,
    val fieldMappings: Map<String, Any?>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class FormResponseRequest(
    val customerId: UUID? = null,
    val customerName: String? = null,
    val status: FormResponseStatus? = null,
    val answers: Map<String, Any?> = emptyMap()
)

data class FormResponseResponseDto(
    val id: UUID,
    val formId: UUID,
    val customerId: UUID?,
    val customerName: String?,
    val status: FormResponseStatus,
    val answers: Map<String, Any?>,
    val generatedReportId: UUID?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
