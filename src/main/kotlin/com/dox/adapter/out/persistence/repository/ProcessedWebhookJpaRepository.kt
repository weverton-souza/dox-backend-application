package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.ProcessedWebhookJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProcessedWebhookJpaRepository : JpaRepository<ProcessedWebhookJpaEntity, UUID> {
    fun existsByAsaasEventId(asaasEventId: String): Boolean
}
