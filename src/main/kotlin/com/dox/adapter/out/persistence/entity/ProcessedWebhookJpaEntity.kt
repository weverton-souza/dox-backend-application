package com.dox.adapter.out.persistence.entity

import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "processed_webhooks", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class ProcessedWebhookJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "asaas_event_id", nullable = false, unique = true, length = 255)
    var asaasEventId: String,
    @Column(name = "event_type", nullable = false, length = 50)
    var eventType: String,
    @Type(JsonType::class)
    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    var payload: Map<String, Any?> = emptyMap(),
    @CreatedDate
    @Column(name = "processed_at", updatable = false)
    var processedAt: LocalDateTime? = null,
)
