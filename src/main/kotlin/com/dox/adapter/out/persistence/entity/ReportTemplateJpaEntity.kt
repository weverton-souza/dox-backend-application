package com.dox.adapter.out.persistence.entity

import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "report_templates")
@EntityListeners(AuditingEntityListener::class)
class ReportTemplateJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "name", nullable = false)
    var name: String = "",
    @Column(name = "description")
    var description: String? = null,
    @Type(JsonType::class)
    @Column(name = "blocks", columnDefinition = "jsonb")
    var blocks: List<Map<String, Any?>> = emptyList(),
    @Column(name = "is_default")
    var isDefault: Boolean = false,
    @Column(name = "is_locked")
    var isLocked: Boolean = false,
    @Column(name = "is_master")
    var isMaster: Boolean = false,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
