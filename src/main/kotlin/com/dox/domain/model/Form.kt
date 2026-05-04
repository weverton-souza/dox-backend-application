package com.dox.domain.model

import com.dox.domain.enum.FormResponseStatus
import com.dox.domain.enum.RespondentType
import java.time.LocalDateTime
import java.util.UUID

data class Form(
    val id: UUID = UUID.randomUUID(),
    val linkedTemplateId: UUID? = null,
    val currentVersion: Int = 1,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)

data class FormVersion(
    val id: UUID = UUID.randomUUID(),
    val formId: UUID,
    val version: Int = 1,
    val title: String,
    val description: String? = null,
    val fields: List<Map<String, Any?>> = emptyList(),
    val fieldMappings: List<Map<String, Any?>> = emptyList(),
    val scoringConfig: Map<String, Any?> = emptyMap(),
    val createdAt: LocalDateTime? = null,
)

data class FormResponse(
    val id: UUID = UUID.randomUUID(),
    val formId: UUID,
    val formVersionId: UUID,
    val customerId: UUID? = null,
    val customerName: String? = null,
    val customerContactId: UUID? = null,
    val respondentType: RespondentType = RespondentType.CUSTOMER,
    val respondentName: String? = null,
    val status: FormResponseStatus = FormResponseStatus.EM_ANDAMENTO,
    val answers: List<Map<String, Any?>> = emptyList(),
    val additionalEvaluators: List<Map<String, Any?>> = emptyList(),
    val pageDurationsMs: Map<String, Long> = emptyMap(),
    val generatedReportId: UUID? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
