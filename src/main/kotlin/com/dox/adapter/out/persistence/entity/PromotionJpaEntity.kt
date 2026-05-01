package com.dox.adapter.out.persistence.entity

import com.dox.domain.billing.AppliesTo
import com.dox.domain.billing.DiscountType
import com.dox.domain.billing.DurationType
import com.dox.domain.billing.PromotionType
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "promotions", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class PromotionJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "code", unique = true, length = 60)
    var code: String? = null,
    @Column(name = "name", nullable = false, length = 150)
    var name: String = "",
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    var type: PromotionType = PromotionType.COUPON,
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 30)
    var discountType: DiscountType = DiscountType.PERCENTAGE,
    @Column(name = "discount_value", nullable = false)
    var discountValue: Int = 0,
    @Enumerated(EnumType.STRING)
    @Column(name = "duration_type", nullable = false, length = 20)
    var durationType: DurationType = DurationType.ONCE,
    @Column(name = "duration_months")
    var durationMonths: Int? = null,
    @Column(name = "max_redemptions")
    var maxRedemptions: Int? = null,
    @Column(name = "current_redemptions", nullable = false)
    var currentRedemptions: Int = 0,
    @Column(name = "valid_from")
    var validFrom: LocalDateTime? = null,
    @Column(name = "valid_until")
    var validUntil: LocalDateTime? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "applies_to", nullable = false, length = 30)
    var appliesTo: AppliesTo = AppliesTo.ALL_MODULES,
    @Type(JsonType::class)
    @Column(name = "applies_to_modules", columnDefinition = "jsonb")
    var appliesToModules: List<String>? = null,
    @Type(JsonType::class)
    @Column(name = "applies_to_verticals", columnDefinition = "jsonb")
    var appliesToVerticals: List<String>? = null,
    @Column(name = "applies_to_signup_after")
    var appliesToSignupAfter: LocalDateTime? = null,
    @Column(name = "applies_to_signup_before")
    var appliesToSignupBefore: LocalDateTime? = null,
    @Type(JsonType::class)
    @Column(name = "stackable_with", columnDefinition = "jsonb", nullable = false)
    var stackableWith: List<String> = emptyList(),
    @Column(name = "skip_proration", nullable = false)
    var skipProration: Boolean = false,
    @Column(name = "requires_approval", nullable = false)
    var requiresApproval: Boolean = false,
    @Column(name = "auto_apply_event", length = 60)
    var autoApplyEvent: String? = null,
    @Column(name = "partner_id")
    var partnerId: UUID? = null,
    @Column(name = "next_promotion_id")
    var nextPromotionId: UUID? = null,
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,
    @Column(name = "created_by_user_id")
    var createdByUserId: UUID? = null,
    @Column(name = "archived_at")
    var archivedAt: LocalDateTime? = null,
)
