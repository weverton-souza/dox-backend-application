package com.dox.application.port.input

import com.dox.domain.billing.AppliesTo
import com.dox.domain.billing.DiscountType
import com.dox.domain.billing.DurationType
import com.dox.domain.billing.Promotion
import com.dox.domain.billing.PromotionType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.util.UUID

data class CreatePromotionCommand(
    val code: String?,
    val name: String,
    val type: PromotionType,
    val discountType: DiscountType,
    val discountValue: Int,
    val durationType: DurationType,
    val durationMonths: Int? = null,
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
    val autoApplyEvent: String? = null,
)

data class UpdatePromotionCommand(
    val name: String? = null,
    val maxRedemptions: Int? = null,
    val validFrom: LocalDateTime? = null,
    val validUntil: LocalDateTime? = null,
    val appliesToModules: List<String>? = null,
    val appliesToVerticals: List<String>? = null,
    val appliesToSignupAfter: LocalDateTime? = null,
    val appliesToSignupBefore: LocalDateTime? = null,
    val stackableWith: List<String>? = null,
    val requiresApproval: Boolean? = null,
    val autoApplyEvent: String? = null,
)

data class PromotionStats(
    val promotionId: UUID,
    val currentRedemptions: Int,
    val maxRedemptions: Int?,
    val activeTenantIds: List<UUID>,
)

interface AdminPromotionUseCase {
    fun listPromotions(
        includeArchived: Boolean,
        pageable: Pageable,
    ): Page<Promotion>

    fun createPromotion(
        command: CreatePromotionCommand,
        actorAdminId: UUID,
    ): Promotion

    fun updatePromotion(
        promotionId: UUID,
        command: UpdatePromotionCommand,
        actorAdminId: UUID,
    ): Promotion

    fun archivePromotion(
        promotionId: UUID,
        actorAdminId: UUID,
    ): Promotion

    fun getStats(promotionId: UUID): PromotionStats
}
