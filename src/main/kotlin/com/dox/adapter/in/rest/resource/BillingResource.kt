package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.billing.AddOrRemoveModuleRequest
import com.dox.adapter.`in`.rest.dto.billing.CancelSubscriptionRequest
import com.dox.adapter.`in`.rest.dto.billing.InvoiceResponse
import com.dox.adapter.`in`.rest.dto.billing.PaymentResponse
import com.dox.adapter.`in`.rest.dto.billing.PriceBreakdownResponse
import com.dox.adapter.`in`.rest.dto.billing.SubscribeBundleRequest
import com.dox.adapter.`in`.rest.dto.billing.SubscribeModulesRequest
import com.dox.adapter.`in`.rest.dto.billing.SubscriptionResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

@Tag(name = "Billing", description = "Cobrança e assinatura DOX via Asaas")
@RequestMapping("/billing")
interface BillingResource : BaseResource {
    @Operation(summary = "Assinar um bundle pré-configurado")
    @PostMapping("/subscribe-bundle")
    fun subscribeBundle(
        @Valid @RequestBody request: SubscribeBundleRequest,
    ): ResponseEntity<SubscriptionResponse>

    @Operation(summary = "Assinar módulos individuais à la carte")
    @PostMapping("/subscribe-modules")
    fun subscribeModules(
        @Valid @RequestBody request: SubscribeModulesRequest,
    ): ResponseEntity<SubscriptionResponse>

    @Operation(summary = "Adicionar módulo à subscription ativa (com proration)")
    @PatchMapping("/subscription/modules/add")
    fun addModule(
        @Valid @RequestBody request: AddOrRemoveModuleRequest,
    ): ResponseEntity<SubscriptionResponse>

    @Operation(summary = "Remover módulo da subscription ativa")
    @PatchMapping("/subscription/modules/remove")
    fun removeModule(
        @Valid @RequestBody request: AddOrRemoveModuleRequest,
    ): ResponseEntity<SubscriptionResponse>

    @Operation(summary = "Cancelar subscription (mantém acesso até fim do período)")
    @DeleteMapping("/subscription")
    fun cancel(
        @RequestBody(required = false) request: CancelSubscriptionRequest?,
    ): ResponseEntity<SubscriptionResponse>

    @Operation(summary = "Reativar subscription cancelada")
    @PostMapping("/subscription/reactivate")
    fun reactivate(): ResponseEntity<SubscriptionResponse>

    @Operation(summary = "Obter subscription atual do tenant")
    @GetMapping("/subscription")
    fun getSubscription(): ResponseEntity<SubscriptionResponse?>

    @Operation(summary = "Listar pagamentos do tenant")
    @GetMapping("/payments")
    fun listPayments(
        @RequestParam(required = false) from: LocalDate?,
        @RequestParam(required = false) to: LocalDate?,
    ): ResponseEntity<List<PaymentResponse>>

    @Operation(summary = "Listar notas fiscais do tenant")
    @GetMapping("/invoices")
    fun listInvoices(): ResponseEntity<List<InvoiceResponse>>

    @Operation(summary = "Pré-visualizar preço sem persistir")
    @GetMapping("/price-preview")
    fun pricePreview(
        @RequestParam moduleIds: List<String>,
        @RequestParam cycle: String,
        @RequestParam(required = false) bundleId: String?,
    ): ResponseEntity<PriceBreakdownResponse>
}
