package com.dox.adapter.out.persistence.entity

import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "bundles", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class BundleJpaEntity(
    @Id
    @Column(name = "id", length = 50, updatable = false)
    var id: String,
    @Column(name = "name", nullable = false, length = 100)
    var name: String,
    @Column(name = "description")
    var description: String? = null,
    @Type(JsonType::class)
    @Column(name = "modules", columnDefinition = "jsonb", nullable = false)
    var modules: List<String> = emptyList(),
    @Column(name = "price_monthly_cents", nullable = false)
    var priceMonthlyCents: Int = 0,
    @Column(name = "price_yearly_cents", nullable = false)
    var priceYearlyCents: Int = 0,
    @Column(name = "seats_included", nullable = false)
    var seatsIncluded: Int = 1,
    @Column(name = "tracking_slots_included", nullable = false)
    var trackingSlotsIncluded: Int = 0,
    @Column(name = "highlighted", nullable = false)
    var highlighted: Boolean = false,
    @Column(name = "active", nullable = false)
    var active: Boolean = true,
    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
