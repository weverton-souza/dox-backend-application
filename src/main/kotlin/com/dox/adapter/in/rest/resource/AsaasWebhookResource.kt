package com.dox.adapter.`in`.rest.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Webhooks", description = "Recebimento de eventos de provedores externos")
@RequestMapping("/webhooks")
interface AsaasWebhookResource : BaseResource {
    @Operation(summary = "Receber evento de cobrança do Asaas")
    @PostMapping("/asaas")
    fun receive(
        @RequestHeader(name = "asaas-access-token", required = false) token: String?,
        @RequestBody payload: Map<String, Any?>,
    ): ResponseEntity<Void>
}
