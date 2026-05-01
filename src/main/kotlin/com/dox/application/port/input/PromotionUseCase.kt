package com.dox.application.port.input

import com.dox.domain.billing.Module
import com.dox.domain.billing.PriceBreakdown
import com.dox.domain.billing.Promotion
import com.dox.domain.billing.TenantPromotion
import java.util.UUID

data class TenantPromotionView(
    val tenantPromotion: TenantPromotion,
    val promotion: Promotion,
)

interface PromotionUseCase {
    fun calculatePrice(
        tenantId: UUID,
        modules: Collection<Module>,
        bundlePriceCents: Int? = null,
    ): PriceBreakdown

    fun applyCoupon(
        tenantId: UUID,
        code: String,
        appliedByUserId: UUID? = null,
    ): TenantPromotionView

    fun revokePromotion(
        tenantId: UUID,
        tenantPromotionId: UUID,
    ): TenantPromotionView

    fun listActive(tenantId: UUID): List<TenantPromotionView>
}
