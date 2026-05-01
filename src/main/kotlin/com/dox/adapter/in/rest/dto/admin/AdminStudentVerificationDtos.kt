package com.dox.adapter.`in`.rest.dto.admin

import com.dox.domain.billing.StudentVerificationStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class AdminStudentVerificationResponse(
    val id: UUID,
    val tenantId: UUID,
    val userId: UUID,
    val documentUrl: String,
    val institution: String?,
    val course: String?,
    val expectedGraduation: LocalDate?,
    val status: StudentVerificationStatus,
    val reviewedByAdminId: UUID?,
    val reviewedAt: LocalDateTime?,
    val notes: String?,
    val rejectionReason: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)

data class CreateStudentVerificationRequest(
    @field:NotNull(message = "Tenant é obrigatório")
    val tenantId: UUID,
    @field:NotNull(message = "User é obrigatório")
    val userId: UUID,
    @field:NotBlank(message = "URL do documento é obrigatória")
    val documentUrl: String,
    @field:Size(max = 200)
    val institution: String? = null,
    @field:Size(max = 200)
    val course: String? = null,
    val expectedGraduation: LocalDate? = null,
    @field:Size(max = 500)
    val notes: String? = null,
)

data class ApproveStudentVerificationRequest(
    @field:Size(max = 500)
    val notes: String? = null,
)

data class RejectStudentVerificationRequest(
    @field:NotBlank(message = "Motivo da rejeição é obrigatório")
    @field:Size(max = 500)
    val rejectionReason: String,
)
