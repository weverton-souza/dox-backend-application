package com.dox.adapter.out.persistence.entity

import com.dox.domain.billing.StudentVerificationStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "student_verifications", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class StudentVerificationJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID,
    @Column(name = "user_id", nullable = false)
    var userId: UUID,
    @Column(name = "document_url", nullable = false, columnDefinition = "TEXT")
    var documentUrl: String,
    @Column(name = "institution", length = 200)
    var institution: String? = null,
    @Column(name = "course", length = 200)
    var course: String? = null,
    @Column(name = "expected_graduation")
    var expectedGraduation: LocalDate? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: StudentVerificationStatus = StudentVerificationStatus.PENDING,
    @Column(name = "reviewed_by_admin_id")
    var reviewedByAdminId: UUID? = null,
    @Column(name = "reviewed_at")
    var reviewedAt: LocalDateTime? = null,
    @Column(name = "notes", columnDefinition = "TEXT")
    var notes: String? = null,
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    var rejectionReason: String? = null,
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null,
)
