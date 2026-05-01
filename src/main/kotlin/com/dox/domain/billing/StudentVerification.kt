package com.dox.domain.billing

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class StudentVerification(
    val id: UUID = UUID.randomUUID(),
    val tenantId: UUID,
    val userId: UUID,
    val documentUrl: String,
    val institution: String? = null,
    val course: String? = null,
    val expectedGraduation: LocalDate? = null,
    val status: StudentVerificationStatus = StudentVerificationStatus.PENDING,
    val reviewedByAdminId: UUID? = null,
    val reviewedAt: LocalDateTime? = null,
    val notes: String? = null,
    val rejectionReason: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
