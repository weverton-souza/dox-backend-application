package com.dox.application.port.input

import com.dox.domain.billing.StudentVerification
import com.dox.domain.billing.StudentVerificationStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.util.UUID

data class CreateStudentVerificationCommand(
    val tenantId: UUID,
    val userId: UUID,
    val documentUrl: String,
    val institution: String? = null,
    val course: String? = null,
    val expectedGraduation: LocalDate? = null,
    val notes: String? = null,
)

interface AdminStudentVerificationUseCase {
    fun list(
        status: StudentVerificationStatus?,
        pageable: Pageable,
    ): Page<StudentVerification>

    fun getById(id: UUID): StudentVerification

    fun createManual(
        command: CreateStudentVerificationCommand,
        actorAdminId: UUID,
    ): StudentVerification

    fun approve(
        id: UUID,
        notes: String?,
        actorAdminId: UUID,
    ): StudentVerification

    fun reject(
        id: UUID,
        rejectionReason: String,
        actorAdminId: UUID,
    ): StudentVerification
}
