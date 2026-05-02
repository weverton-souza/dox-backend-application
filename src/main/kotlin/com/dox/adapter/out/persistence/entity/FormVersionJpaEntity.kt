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
@Table(name = "form_versions")
@EntityListeners(AuditingEntityListener::class)
class FormVersionJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "form_id", nullable = false)
    var formId: UUID = UUID.randomUUID(),
    @Column(name = "version", nullable = false)
    var version: Int = 1,
    @Column(name = "title", nullable = false)
    var title: String = "",
    @Column(name = "description")
    var description: String? = null,
    @Type(JsonType::class)
    @Column(name = "fields", columnDefinition = "jsonb")
    var fields: List<Map<String, Any?>> = emptyList(),
    @Type(JsonType::class)
    @Column(name = "field_mappings", columnDefinition = "jsonb")
    var fieldMappings: List<Map<String, Any?>> = emptyList(),
    @Type(JsonType::class)
    @Column(name = "scoring_config", columnDefinition = "jsonb")
    var scoringConfig: Map<String, Any?> = emptyMap(),
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
)
