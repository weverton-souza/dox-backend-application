package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.billing.ApplyCouponRequest
import com.dox.adapter.`in`.rest.dto.billing.PromotionSummary
import com.dox.adapter.`in`.rest.dto.billing.TenantPromotionResponse
import com.dox.adapter.`in`.rest.resource.PromotionResource
import com.dox.application.port.input.PromotionUseCase
import com.dox.application.port.input.TenantPromotionView
import com.dox.shared.ContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class PromotionResourceImpl(
    private val promotionUseCase: PromotionUseCase,
) : PromotionResource {
    override fun applyCoupon(request: ApplyCouponRequest): ResponseEntity<TenantPromotionResponse> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        val userId = ContextHolder.context.userId
        val view = promotionUseCase.applyCoupon(tenantId, request.code, userId)
        return responseEntity(view.toResponse(), HttpStatus.CREATED)
    }

    override fun listActive(): ResponseEntity<List<TenantPromotionResponse>> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        val list = promotionUseCase.listActive(tenantId).map { it.toResponse() }
        return responseEntity(list)
    }

    override fun revoke(tenantPromotionId: UUID): ResponseEntity<TenantPromotionResponse> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        val view = promotionUseCase.revokePromotion(tenantId, tenantPromotionId)
        return responseEntity(view.toResponse())
    }

    private fun TenantPromotionView.toResponse() =
        TenantPromotionResponse(
            id = tenantPromotion.id,
            tenantId = tenantPromotion.tenantId,
            promotion =
                PromotionSummary(
                    id = promotion.id,
                    code = promotion.code,
                    name = promotion.name,
                    type = promotion.type,
                    discountType = promotion.discountType,
                    discountValue = promotion.discountValue,
                    durationType = promotion.durationType,
                    durationMonths = promotion.durationMonths,
                    appliesTo = promotion.appliesTo,
                ),
            appliedAt = tenantPromotion.appliedAt,
            expiresAt = tenantPromotion.expiresAt,
            status = tenantPromotion.status,
            sourceEvent = tenantPromotion.sourceEvent,
            notes = tenantPromotion.notes,
        )
}
