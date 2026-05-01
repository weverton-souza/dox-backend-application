package com.dox.adapter.`in`.rest.dto.billing

import com.dox.domain.billing.AppliesTo
import com.dox.domain.billing.DiscountType
import com.dox.domain.billing.DurationType
import com.dox.domain.billing.PromotionType
import com.dox.domain.billing.TenantPromotionStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

data class ApplyCouponRequest(
    @field:NotBlank(message = "Código é obrigatório")
    @field:Size(max = 60, message = "Código muito longo")
    val code: String,
)

data class PromotionSummary(
    val id: UUID,
    val code: String?,
    val name: String,
    val type: PromotionType,
    val discountType: DiscountType,
    val discountValue: Int,
    val durationType: DurationType,
    val durationMonths: Int?,
    val appliesTo: AppliesTo,
)

data class TenantPromotionResponse(
    val id: UUID,
    val tenantId: UUID,
    val promotion: PromotionSummary,
    val appliedAt: LocalDateTime,
    val expiresAt: LocalDateTime?,
    val status: TenantPromotionStatus,
    val sourceEvent: String?,
    val notes: String?,
)
