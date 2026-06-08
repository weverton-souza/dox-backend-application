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
@Table(name = "tenant_addons", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class TenantAddonJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID = UUID.randomUUID(),
    @Column(name = "addon_id", nullable = false, length = 50)
    var addonId: String = "",
    @Column(name = "quantity", nullable = false)
    var quantity: Int = 1,
    @Column(name = "activated_at", nullable = false)
    var activatedAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "canceled_at")
    var canceledAt: LocalDateTime? = null,
    @Column(name = "base_price_cents", nullable = false)
    var basePriceCents: Int = 0,
    @Column(name = "final_price_cents", nullable = false)
    var finalPriceCents: Int = 0,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
