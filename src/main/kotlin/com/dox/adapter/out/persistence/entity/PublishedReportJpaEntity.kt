package com.dox.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "published_reports", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class PublishedReportJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "report_id", nullable = false)
    var reportId: UUID,
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID,
    @Column(name = "verification_code", nullable = false, unique = true, length = 16)
    var verificationCode: String,
    @Column(name = "content_hash", nullable = false, length = 64)
    var contentHash: String,
    @Column(name = "finalized_at", nullable = false)
    var finalizedAt: LocalDateTime,
    @Column(name = "professional_name", length = 255)
    var professionalName: String? = null,
    @Column(name = "professional_council", length = 100)
    var professionalCouncil: String? = null,
    @Column(name = "customer_initials", length = 20)
    var customerInitials: String? = null,
    @CreatedDate
    @Column(name = "published_at", updatable = false)
    var publishedAt: LocalDateTime? = null,
)
