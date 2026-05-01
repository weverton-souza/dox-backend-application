package com.dox.application.service

import com.dox.application.port.input.PromotionUseCase
import com.dox.application.port.input.TenantPromotionView
import com.dox.application.port.output.PromotionPersistencePort
import com.dox.application.port.output.TenantPersistencePort
import com.dox.application.port.output.TenantPromotionPersistencePort
import com.dox.domain.billing.AppliedDiscount
import com.dox.domain.billing.AppliesTo
import com.dox.domain.billing.BillingCalculator
import com.dox.domain.billing.DiscountType
import com.dox.domain.billing.Module
import com.dox.domain.billing.PriceBreakdown
import com.dox.domain.billing.Promotion
import com.dox.domain.billing.TenantPromotion
import com.dox.domain.billing.TenantPromotionStatus
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.Tenant
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class PromotionEngineServiceImpl(
    private val promotionPersistencePort: PromotionPersistencePort,
    private val tenantPromotionPersistencePort: TenantPromotionPersistencePort,
    private val tenantPersistencePort: TenantPersistencePort,
) : PromotionUseCase {
    @Transactional(readOnly = true)
    override fun calculatePrice(
        tenantId: UUID,
        modules: Collection<Module>,
        bundlePriceCents: Int?,
    ): PriceBreakdown {
        val baseBreakdown = BillingCalculator.breakdown(modules, bundlePriceCents = bundlePriceCents)
        val moduleIds = modules.map { it.id }.toSet()

        val tenant = tenantPersistencePort.findById(tenantId)
        val activeViews = listActive(tenantId)
        val applicable =
            activeViews
                .filter { isApplicable(it.promotion, moduleIds, tenant) }
                .map { it }

        val stackResolved = resolveStacking(applicable)
        val ordered = stackResolved.sortedBy { it.promotion.discountType.applicationOrder }

        var runningPrice = baseBreakdown.finalPriceCents
        val applied = mutableListOf<AppliedDiscount>()
        for (view in ordered) {
            val amount = computeDiscount(view.promotion, runningPrice)
            if (amount <= 0) continue
            runningPrice = (runningPrice - amount).coerceAtLeast(0)
            applied +=
                AppliedDiscount(
                    promotionId = view.promotion.id,
                    tenantPromotionId = view.tenantPromotion.id,
                    code = view.promotion.code,
                    name = view.promotion.name,
                    type = view.promotion.type,
                    discountType = view.promotion.discountType,
                    discountValue = view.promotion.discountValue,
                    amountCents = amount,
                )
        }

        return baseBreakdown.copy(
            finalPriceCents = runningPrice,
            appliedPromotions = applied,
        )
    }

    @Transactional
    override fun applyCoupon(
        tenantId: UUID,
        code: String,
        appliedByUserId: UUID?,
    ): TenantPromotionView {
        val promotion =
            promotionPersistencePort.findByCode(code.trim())
                ?: throw BusinessException("Cupom inválido")

        if (promotion.isArchived) {
            throw BusinessException("Cupom indisponível")
        }
        if (promotion.requiresApproval) {
            throw BusinessException("Cupom requer aprovação manual")
        }

        val tenant =
            tenantPersistencePort.findById(tenantId)
                ?: throw ResourceNotFoundException("Tenant", tenantId.toString())

        if (!matchesSegmentation(promotion, tenant)) {
            throw BusinessException("Cupom não disponível para este perfil")
        }

        if (tenantPromotionPersistencePort.findByTenantIdAndPromotionId(tenantId, promotion.id) != null) {
            throw BusinessException("Cupom já aplicado neste tenant")
        }

        val now = LocalDateTime.now()
        val incremented = promotionPersistencePort.tryIncrementRedemption(promotion.id, now)
        if (!incremented) {
            throw BusinessException("Cupom esgotado ou fora do período de validade")
        }

        val expiresAt = computeExpiration(promotion, now)
        val saved =
            tenantPromotionPersistencePort.save(
                TenantPromotion(
                    tenantId = tenantId,
                    promotionId = promotion.id,
                    appliedAt = now,
                    expiresAt = expiresAt,
                    appliedByUserId = appliedByUserId,
                    sourceEvent = "COUPON_REDEMPTION",
                    status = TenantPromotionStatus.ACTIVE,
                ),
            )

        val refreshed = promotionPersistencePort.findById(promotion.id) ?: promotion
        return TenantPromotionView(saved, refreshed)
    }

    @Transactional
    override fun revokePromotion(
        tenantId: UUID,
        tenantPromotionId: UUID,
    ): TenantPromotionView {
        val existing =
            tenantPromotionPersistencePort.findById(tenantPromotionId)
                ?: throw ResourceNotFoundException("TenantPromotion", tenantPromotionId.toString())
        if (existing.tenantId != tenantId) {
            throw ResourceNotFoundException("TenantPromotion", tenantPromotionId.toString())
        }
        if (existing.status != TenantPromotionStatus.ACTIVE) {
            throw BusinessException("Promoção já inativa")
        }

        val updated =
            tenantPromotionPersistencePort.save(existing.copy(status = TenantPromotionStatus.REVOKED))
        val promotion =
            promotionPersistencePort.findById(updated.promotionId)
                ?: throw ResourceNotFoundException("Promotion", updated.promotionId.toString())
        return TenantPromotionView(updated, promotion)
    }

    @Transactional(readOnly = true)
    override fun listActive(tenantId: UUID): List<TenantPromotionView> {
        val now = LocalDateTime.now()
        val active = tenantPromotionPersistencePort.findActiveByTenantId(tenantId)
        return active.mapNotNull { tp ->
            if (tp.expiresAt != null && tp.expiresAt.isBefore(now)) return@mapNotNull null
            val promotion = promotionPersistencePort.findById(tp.promotionId) ?: return@mapNotNull null
            TenantPromotionView(tp, promotion)
        }
    }

    private fun isApplicable(
        promotion: Promotion,
        moduleIds: Set<String>,
        tenant: Tenant?,
    ): Boolean {
        if (!matchesSegmentation(promotion, tenant)) return false
        return when (promotion.appliesTo) {
            AppliesTo.ALL_MODULES -> true
            AppliesTo.SPECIFIC_MODULES -> promotion.appliesToModules.any { moduleIds.contains(it) }
            AppliesTo.MIN_BUNDLE -> true
            AppliesTo.FIRST_PAYMENT_ONLY -> true
        }
    }

    private fun matchesSegmentation(
        promotion: Promotion,
        tenant: Tenant?,
    ): Boolean {
        if (tenant == null) return true
        if (promotion.appliesToVerticals.isNotEmpty() &&
            !promotion.appliesToVerticals.contains(tenant.vertical.name)
        ) {
            return false
        }
        val tenantSignup = tenant.createdAt
        if (promotion.appliesToSignupAfter != null &&
            (tenantSignup == null || tenantSignup.isBefore(promotion.appliesToSignupAfter))
        ) {
            return false
        }
        if (promotion.appliesToSignupBefore != null &&
            (tenantSignup == null || tenantSignup.isAfter(promotion.appliesToSignupBefore))
        ) {
            return false
        }
        return true
    }

    private fun resolveStacking(views: List<TenantPromotionView>): List<TenantPromotionView> {
        val (stackables, exclusives) = views.partition { it.promotion.stackableWith.isNotEmpty() }
        if (exclusives.isEmpty()) return stackables
        val bestExclusive =
            exclusives.maxByOrNull { effectiveDiscount(it.promotion) }
        return if (bestExclusive == null) stackables else stackables + bestExclusive
    }

    private fun effectiveDiscount(promotion: Promotion): Int =
        when (promotion.discountType) {
            DiscountType.FIXED_AMOUNT -> promotion.discountValue
            DiscountType.PERCENTAGE -> promotion.discountValue
            DiscountType.FREE_MONTHS -> promotion.discountValue * 100
            DiscountType.TRIAL_EXTENSION_DAYS -> 0
        }

    private fun computeDiscount(
        promotion: Promotion,
        runningPriceCents: Int,
    ): Int =
        when (promotion.discountType) {
            DiscountType.FIXED_AMOUNT -> promotion.discountValue.coerceAtMost(runningPriceCents)
            DiscountType.PERCENTAGE ->
                (runningPriceCents.toLong() * promotion.discountValue / 100).toInt()
                    .coerceAtMost(runningPriceCents)
            DiscountType.FREE_MONTHS -> runningPriceCents
            DiscountType.TRIAL_EXTENSION_DAYS -> 0
        }

    private fun computeExpiration(
        promotion: Promotion,
        appliedAt: LocalDateTime,
    ): LocalDateTime? =
        when (promotion.durationType) {
            com.dox.domain.billing.DurationType.ONCE -> appliedAt
            com.dox.domain.billing.DurationType.FOREVER -> null
            com.dox.domain.billing.DurationType.FIXED_MONTHS ->
                promotion.durationMonths?.let { appliedAt.plusMonths(it.toLong()) }
        }
}
