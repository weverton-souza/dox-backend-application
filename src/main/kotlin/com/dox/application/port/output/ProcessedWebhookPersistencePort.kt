package com.dox.application.port.output

interface ProcessedWebhookPersistencePort {
    fun isProcessed(asaasEventId: String): Boolean

    fun markProcessed(
        asaasEventId: String,
        eventType: String,
        payload: Map<String, Any?>,
    )
}
