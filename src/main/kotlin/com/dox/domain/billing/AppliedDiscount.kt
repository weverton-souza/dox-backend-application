package com.dox.domain.billing

import java.util.UUID

data class AppliedDiscount(
    val promotionId: UUID,
    val tenantPromotionId: UUID? = null,
    val code: String? = null,
    val name: String,
    val type: PromotionType,
    val discountType: DiscountType,
    val discountValue: Int,
    val amountCents: Int,
)
