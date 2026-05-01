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
@Table(name = "billing_audit_log", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class BillingAuditLogJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id")
    var tenantId: UUID? = null,
    @Column(name = "actor_admin_id", nullable = false)
    var actorAdminId: UUID,
    @Column(name = "action", nullable = false, length = 50)
    var action: String,
    @Type(JsonType::class)
    @Column(name = "before_state", columnDefinition = "jsonb")
    var beforeState: Map<String, Any?>? = null,
    @Type(JsonType::class)
    @Column(name = "after_state", columnDefinition = "jsonb")
    var afterState: Map<String, Any?>? = null,
    @Column(name = "notes", columnDefinition = "TEXT")
    var notes: String? = null,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
)
