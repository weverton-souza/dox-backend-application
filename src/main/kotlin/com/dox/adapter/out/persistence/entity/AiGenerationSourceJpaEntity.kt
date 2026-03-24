package com.dox.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "ai_generation_sources")
@EntityListeners(AuditingEntityListener::class)
class AiGenerationSourceJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "report_id", nullable = false)
    var reportId: UUID = UUID.randomUUID(),

    @Column(name = "generation_id", nullable = false)
    var generationId: UUID = UUID.randomUUID(),

    @Column(name = "source_type", nullable = false)
    var sourceType: String = "",

    @Column(name = "source_id", nullable = false)
    var sourceId: UUID = UUID.randomUUID(),

    @Column(name = "source_label")
    var sourceLabel: String? = null,

    @Column(name = "included", nullable = false)
    var included: Boolean = true,

    @Column(name = "display_order", nullable = false)
    var displayOrder: Int = 0,

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null
)
