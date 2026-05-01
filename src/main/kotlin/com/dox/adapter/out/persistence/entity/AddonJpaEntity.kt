package com.dox.adapter.out.persistence.entity

import com.dox.domain.billing.AddonType
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "addons", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class AddonJpaEntity(
    @Id
    @Column(name = "id", length = 50, updatable = false)
    var id: String = "",
    @Column(name = "name", nullable = false, length = 100)
    var name: String = "",
    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    var type: AddonType = AddonType.MODULE,
    @Column(name = "target_module_id", length = 50)
    var targetModuleId: String? = null,
    @Column(name = "price_monthly_cents", nullable = false)
    var priceMonthlyCents: Int = 0,
    @Column(name = "price_unit_cents")
    var priceUnitCents: Int? = null,
    @Column(name = "fee_percentage")
    var feePercentage: BigDecimal? = null,
    @Type(JsonType::class)
    @Column(name = "available_for_bundles", columnDefinition = "jsonb", nullable = false)
    var availableForBundles: List<String> = emptyList(),
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
