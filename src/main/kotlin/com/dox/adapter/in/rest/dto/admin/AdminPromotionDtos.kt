package com.dox.adapter.`in`.rest.dto.admin

import com.dox.domain.billing.AppliesTo
import com.dox.domain.billing.DiscountType
import com.dox.domain.billing.DurationType
import com.dox.domain.billing.PromotionType
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

data class AdminPromotionResponse(
    val id: UUID,
    val code: String?,
    val name: String,
    val type: PromotionType,
    val discountType: DiscountType,
    val discountValue: Int,
    val durationType: DurationType,
    val durationMonths: Int?,
    val maxRedemptions: Int?,
    val currentRedemptions: Int,
    val validFrom: LocalDateTime?,
    val validUntil: LocalDateTime?,
    val appliesTo: AppliesTo,
    val appliesToModules: List<String>,
    val appliesToVerticals: List<String>,
    val appliesToSignupAfter: LocalDateTime?,
    val appliesToSignupBefore: LocalDateTime?,
    val stackableWith: List<String>,
    val skipProration: Boolean,
    val requiresApproval: Boolean,
    val autoApplyEvent: String?,
    val createdAt: LocalDateTime?,
    val createdByUserId: UUID?,
    val archivedAt: LocalDateTime?,
)

data class CreatePromotionRequest(
    @field:Size(max = 60)
    val code: String? = null,
    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(max = 150)
    val name: String,
    val type: PromotionType,
    val discountType: DiscountType,
    @field:Min(value = 0, message = "Valor do desconto não pode ser negativo")
    val discountValue: Int,
    val durationType: DurationType,
    @field:Min(value = 1)
    val durationMonths: Int? = null,
    @field:Min(value = 1)
    val maxRedemptions: Int? = null,
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
    @field:Size(max = 60)
    val autoApplyEvent: String? = null,
)

data class UpdatePromotionRequest(
    @field:Size(max = 150)
    val name: String? = null,
    @field:Min(value = 1)
    val maxRedemptions: Int? = null,
    val validFrom: LocalDateTime? = null,
    val validUntil: LocalDateTime? = null,
    val appliesToModules: List<String>? = null,
    val appliesToVerticals: List<String>? = null,
    val appliesToSignupAfter: LocalDateTime? = null,
    val appliesToSignupBefore: LocalDateTime? = null,
    val stackableWith: List<String>? = null,
    val requiresApproval: Boolean? = null,
    @field:Size(max = 60)
    val autoApplyEvent: String? = null,
)

data class AdminPromotionStatsResponse(
    val promotionId: UUID,
    val currentRedemptions: Int,
    val maxRedemptions: Int?,
    val activeTenantIds: List<UUID>,
)
