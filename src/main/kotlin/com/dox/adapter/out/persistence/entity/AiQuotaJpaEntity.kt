package com.dox.adapter.out.persistence.entity

import com.dox.domain.enum.AiTier
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
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "ai_quotas")
@EntityListeners(AuditingEntityListener::class)
class AiQuotaJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Enumerated(EnumType.STRING)
    @Column(name = "ai_tier", nullable = false)
    var aiTier: AiTier = AiTier.NONE,
    @Column(name = "model", nullable = false)
    var model: String = "claude-sonnet-4-6",
    @Column(name = "monthly_limit", nullable = false)
    var monthlyLimit: Int = 0,
    @Column(name = "overage_price_cents", nullable = false)
    var overagePriceCents: Int = 0,
    @Column(name = "enabled", nullable = false)
    var enabled: Boolean = false,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
