package com.dox.application.port.input

interface WebhookUseCase {
    fun handleAsaasEvent(
        receivedToken: String?,
        payload: Map<String, Any?>,
    )
}
