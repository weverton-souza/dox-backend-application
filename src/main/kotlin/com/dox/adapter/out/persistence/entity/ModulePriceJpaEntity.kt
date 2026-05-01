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
@Table(name = "module_prices", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class ModulePriceJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "module_id", nullable = false, length = 50)
    var moduleId: String = "",
    @Column(name = "price_cents", nullable = false)
    var priceCents: Int = 0,
    @Column(name = "currency", nullable = false, length = 3)
    var currency: String = "BRL",
    @Column(name = "valid_from", nullable = false)
    var validFrom: LocalDateTime = LocalDateTime.now(),
    @Column(name = "valid_until")
    var validUntil: LocalDateTime? = null,
    @Column(name = "notes", columnDefinition = "TEXT")
    var notes: String? = null,
    @Column(name = "created_by_user_id")
    var createdByUserId: UUID? = null,
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,
)
