package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.billing.ApplyCouponRequest
import com.dox.adapter.`in`.rest.dto.billing.TenantPromotionResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Tag(name = "Billing · Promoções", description = "Aplicação e gestão de promoções no tenant")
@RequestMapping("/billing")
interface PromotionResource : BaseResource {
    @Operation(summary = "Aplica um cupom no tenant autenticado")
    @PostMapping("/apply-coupon")
    fun applyCoupon(
        @Valid @RequestBody request: ApplyCouponRequest,
    ): ResponseEntity<TenantPromotionResponse>

    @Operation(summary = "Lista promoções ativas do tenant")
    @GetMapping("/promotions/active")
    fun listActive(): ResponseEntity<List<TenantPromotionResponse>>

    @Operation(summary = "Revoga uma promoção do tenant")
    @DeleteMapping("/promotions/{tenantPromotionId}")
    fun revoke(
        @PathVariable tenantPromotionId: UUID,
    ): ResponseEntity<TenantPromotionResponse>
}
