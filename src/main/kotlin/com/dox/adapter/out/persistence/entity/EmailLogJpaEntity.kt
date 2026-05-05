package com.dox.adapter.out.persistence.entity

import com.dox.domain.email.EmailLogStatus
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "email_log", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class EmailLogJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id")
    var tenantId: UUID? = null,
    @Column(name = "template_id", nullable = false)
    var templateId: String = "",
    @Column(name = "recipient_email", nullable = false)
    var recipientEmail: String = "",
    @Column(name = "subject", nullable = false)
    var subject: String = "",
    @Column(name = "provider_id")
    var providerId: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: EmailLogStatus = EmailLogStatus.PENDING,
    @Column(name = "error_message", columnDefinition = "TEXT")
    var errorMessage: String? = null,
    @Column(name = "idempotency_key")
    var idempotencyKey: String? = null,
    @Type(JsonType::class)
    @Column(name = "tags", columnDefinition = "jsonb")
    var tags: Map<String, String> = emptyMap(),
    @CreatedDate
    @Column(name = "sent_at", nullable = false, updatable = false)
    var sentAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null,
)
