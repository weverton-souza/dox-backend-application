package com.dox.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "tenant_modules", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class TenantModuleJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID,
    @Column(name = "module_id", nullable = false, length = 50)
    var moduleId: String,
    @Column(name = "status", nullable = false, length = 20)
    var status: String,
    @Column(name = "source", nullable = false, length = 20)
    var source: String,
    @Column(name = "source_id", length = 255)
    var sourceId: String? = null,
    @Column(name = "activated_at", nullable = false)
    var activatedAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "expires_at")
    var expiresAt: LocalDateTime? = null,
    @Column(name = "grace_until")
    var graceUntil: LocalDateTime? = null,
    @Column(name = "base_price_cents", nullable = false)
    var basePriceCents: Int = 0,
    @Column(name = "final_price_cents", nullable = false)
    var finalPriceCents: Int = 0,
    @Column(name = "price_locked", nullable = false)
    var priceLocked: Boolean = true,
    @Column(name = "price_locked_at")
    var priceLockedAt: LocalDateTime? = null,
    @Column(name = "canceled_at")
    var canceledAt: LocalDateTime? = null,
    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    var cancelReason: String? = null,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
