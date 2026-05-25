package com.dox.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Assessment(
    val id: UUID = UUID.randomUUID(),
    val customerId: UUID,
    val appointmentId: UUID? = null,
    val applierId: UUID,
    val title: String,
    val category: String? = null,
    val appliedAt: LocalDate,
    val notes: String? = null,
    val parentAssessmentId: UUID? = null,
    val professionalDeclarationAcceptedAt: LocalDateTime,
    val professionalDeclarationRevision: Int = 1,
    val entries: List<AssessmentEntry> = emptyList(),
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
