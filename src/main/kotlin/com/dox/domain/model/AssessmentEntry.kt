package com.dox.domain.model

import com.dox.domain.enum.AssessmentEntryType
import java.time.LocalDateTime
import java.util.UUID

data class AssessmentEntry(
    val id: UUID = UUID.randomUUID(),
    val assessmentId: UUID,
    val instrumentName: String,
    val entryType: AssessmentEntryType,
    val orderIndex: Int = 0,
    val scores: List<AssessmentScore> = emptyList(),
    val block: Map<String, Any?>? = null,
    val observations: String? = null,
    val attachmentFileId: UUID? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
