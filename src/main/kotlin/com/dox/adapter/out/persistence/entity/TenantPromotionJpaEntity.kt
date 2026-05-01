package com.dox.adapter.out.persistence.entity

import com.dox.domain.billing.TenantPromotionStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "tenant_promotions", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class TenantPromotionJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID,
    @Column(name = "promotion_id", nullable = false)
    var promotionId: UUID,
    @Column(name = "applied_at", nullable = false)
    var appliedAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "expires_at")
    var expiresAt: LocalDateTime? = null,
    @Column(name = "applied_by_user_id")
    var appliedByUserId: UUID? = null,
    @Column(name = "source_event", length = 60)
    var sourceEvent: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: TenantPromotionStatus = TenantPromotionStatus.ACTIVE,
    @Column(name = "notes", columnDefinition = "TEXT")
    var notes: String? = null,
    @CreatedDate
    @Column(name = "created_at", insertable = false, updatable = false)
    var createdAt: LocalDateTime? = null,
)
