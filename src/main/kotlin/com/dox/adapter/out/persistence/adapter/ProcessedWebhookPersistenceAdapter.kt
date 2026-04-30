package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.ProcessedWebhookJpaEntity
import com.dox.adapter.out.persistence.repository.ProcessedWebhookJpaRepository
import com.dox.application.port.output.ProcessedWebhookPersistencePort
import org.springframework.stereotype.Component

@Component
class ProcessedWebhookPersistenceAdapter(
    private val repository: ProcessedWebhookJpaRepository,
) : ProcessedWebhookPersistencePort {
    override fun isProcessed(asaasEventId: String): Boolean = repository.existsByAsaasEventId(asaasEventId)

    override fun markProcessed(
        asaasEventId: String,
        eventType: String,
        payload: Map<String, Any?>,
    ) {
        repository.save(
            ProcessedWebhookJpaEntity(
                asaasEventId = asaasEventId,
                eventType = eventType,
                payload = payload,
            ),
        )
    }
}
