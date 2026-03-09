package com.dox.domain.model

import com.dox.domain.enum.FormResponseStatus
import java.time.LocalDateTime
import java.util.UUID

data class Form(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String? = null,
    val fields: List<Map<String, Any?>> = emptyList(),
    val linkedTemplateId: UUID? = null,
    val fieldMappings: Map<String, Any?> = emptyMap(),
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

data class FormResponse(
    val id: UUID = UUID.randomUUID(),
    val formId: UUID,
    val customerId: UUID? = null,
    val customerName: String? = null,
    val status: FormResponseStatus = FormResponseStatus.EM_ANDAMENTO,
    val answers: Map<String, Any?> = emptyMap(),
    val generatedReportId: UUID? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
