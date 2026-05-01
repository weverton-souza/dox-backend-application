package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.StudentVerificationJpaEntity
import com.dox.adapter.out.persistence.repository.StudentVerificationJpaRepository
import com.dox.application.port.output.StudentVerificationPersistencePort
import com.dox.domain.billing.StudentVerification
import com.dox.domain.billing.StudentVerificationStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class StudentVerificationPersistenceAdapter(
    private val repository: StudentVerificationJpaRepository,
) : StudentVerificationPersistencePort {
    override fun findById(id: UUID): StudentVerification? = repository.findById(id).orElse(null)?.toDomain()

    override fun save(verification: StudentVerification): StudentVerification {
        val entity =
            repository.findById(verification.id).orElseGet {
                StudentVerificationJpaEntity(
                    id = verification.id,
                    tenantId = verification.tenantId,
                    userId = verification.userId,
                    documentUrl = verification.documentUrl,
                )
            }
        entity.tenantId = verification.tenantId
        entity.userId = verification.userId
        entity.documentUrl = verification.documentUrl
        entity.institution = verification.institution
        entity.course = verification.course
        entity.expectedGraduation = verification.expectedGraduation
        entity.status = verification.status
        entity.reviewedByAdminId = verification.reviewedByAdminId
        entity.reviewedAt = verification.reviewedAt
        entity.notes = verification.notes
        entity.rejectionReason = verification.rejectionReason
        return repository.save(entity).toDomain()
    }

    override fun findPaginated(
        status: StudentVerificationStatus?,
        pageable: Pageable,
    ): Page<StudentVerification> {
        val page =
            if (status != null) repository.findAllByStatus(status, pageable) else repository.findAll(pageable)
        return page.map { it.toDomain() }
    }

    private fun StudentVerificationJpaEntity.toDomain() =
        StudentVerification(
            id = id,
            tenantId = tenantId,
            userId = userId,
            documentUrl = documentUrl,
            institution = institution,
            course = course,
            expectedGraduation = expectedGraduation,
            status = status,
            reviewedByAdminId = reviewedByAdminId,
            reviewedAt = reviewedAt,
            notes = notes,
            rejectionReason = rejectionReason,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
