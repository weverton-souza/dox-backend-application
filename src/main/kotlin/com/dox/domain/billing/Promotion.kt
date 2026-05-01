package com.dox.domain.billing

import java.time.LocalDateTime
import java.util.UUID

data class Promotion(
    val id: UUID = UUID.randomUUID(),
    val code: String? = null,
    val name: String,
    val type: PromotionType,
    val discountType: DiscountType,
    val discountValue: Int,
    val durationType: DurationType,
    val durationMonths: Int? = null,
    val maxRedemptions: Int? = null,
    val currentRedemptions: Int = 0,
    val validFrom: LocalDateTime? = null,
    val validUntil: LocalDateTime? = null,
    val appliesTo: AppliesTo = AppliesTo.ALL_MODULES,
    val appliesToModules: List<String> = emptyList(),
    val appliesToVerticals: List<String> = emptyList(),
    val appliesToSignupAfter: LocalDateTime? = null,
    val appliesToSignupBefore: LocalDateTime? = null,
    val stackableWith: List<String> = emptyList(),
    val skipProration: Boolean = false,
    val requiresApproval: Boolean = false,
    val autoApplyEvent: String? = null,
    val partnerId: UUID? = null,
    val nextPromotionId: UUID? = null,
    val createdAt: LocalDateTime? = null,
    val createdByUserId: UUID? = null,
    val archivedAt: LocalDateTime? = null,
) {
    val isArchived: Boolean get() = archivedAt != null
    val hasRedemptionsLeft: Boolean
        get() = maxRedemptions == null || currentRedemptions < maxRedemptions
}
