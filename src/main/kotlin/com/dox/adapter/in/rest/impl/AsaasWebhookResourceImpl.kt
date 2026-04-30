package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.resource.AsaasWebhookResource
import com.dox.application.port.input.WebhookUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AsaasWebhookResourceImpl(
    private val webhookUseCase: WebhookUseCase,
) : AsaasWebhookResource {
    override fun receive(
        token: String?,
        payload: Map<String, Any?>,
    ): ResponseEntity<Void> {
        webhookUseCase.handleAsaasEvent(token, payload)
        return noContent()
    }
}
