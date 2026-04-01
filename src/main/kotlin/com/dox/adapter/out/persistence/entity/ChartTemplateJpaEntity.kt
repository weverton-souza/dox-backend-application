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
@Table(name = "chart_templates")
@EntityListeners(AuditingEntityListener::class)
class ChartTemplateJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "name", nullable = false)
    var name: String = "",
    @Column(name = "description")
    var description: String? = null,
    @Column(name = "instrument_name")
    var instrumentName: String? = null,
    @Column(name = "category")
    var category: String? = null,
    @Type(JsonType::class)
    @Column(name = "data", columnDefinition = "jsonb")
    var data: Map<String, Any?> = emptyMap(),
    @Column(name = "is_default")
    var isDefault: Boolean = false,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
