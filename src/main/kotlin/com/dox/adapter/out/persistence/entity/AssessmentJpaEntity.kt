package com.dox.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "assessments")
@EntityListeners(AuditingEntityListener::class)
@SQLRestriction("deleted = false")
class AssessmentJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "customer_id", nullable = false)
    var customerId: UUID = UUID.randomUUID(),
    @Column(name = "appointment_id")
    var appointmentId: UUID? = null,
    @Column(name = "applier_id", nullable = false)
    var applierId: UUID = UUID.randomUUID(),
    @Column(name = "title", nullable = false)
    var title: String = "",
    @Column(name = "category")
    var category: String? = null,
    @Column(name = "applied_at", nullable = false)
    var appliedAt: LocalDate = LocalDate.now(),
    @Column(name = "notes")
    var notes: String? = null,
    @Column(name = "parent_assessment_id")
    var parentAssessmentId: UUID? = null,
    @Column(name = "professional_declaration_accepted_at", nullable = false)
    var professionalDeclarationAcceptedAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "professional_declaration_revision", nullable = false)
    var professionalDeclarationRevision: Int = 1,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
    @Column(name = "deleted", nullable = false)
    var deleted: Boolean = false,
)
