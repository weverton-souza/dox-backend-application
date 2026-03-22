package com.dox.adapter.out.persistence.entity

import com.dox.domain.enum.AiGenerationStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "ai_usages")
@EntityListeners(AuditingEntityListener::class)
class AiUsageJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "report_id")
    var reportId: UUID? = null,

    @Column(name = "generation_id", nullable = false)
    var generationId: UUID = UUID.randomUUID(),

    @Column(name = "professional_id", nullable = false)
    var professionalId: UUID = UUID.randomUUID(),

    @Column(name = "section_type", nullable = false)
    var sectionType: String = "",

    @Column(name = "model", nullable = false)
    var model: String = "",

    @Column(name = "input_tokens", nullable = false)
    var inputTokens: Int = 0,

    @Column(name = "output_tokens", nullable = false)
    var outputTokens: Int = 0,

    @Column(name = "cache_read_tokens", nullable = false)
    var cacheReadTokens: Int = 0,

    @Column(name = "cache_write_tokens", nullable = false)
    var cacheWriteTokens: Int = 0,

    @Column(name = "estimated_cost_brl", nullable = false)
    var estimatedCostBrl: BigDecimal = BigDecimal.ZERO,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: AiGenerationStatus = AiGenerationStatus.SUCCESS,

    @Column(name = "error_message")
    var errorMessage: String? = null,

    @Column(name = "duration_ms", nullable = false)
    var durationMs: Int = 0,

    @Column(name = "is_regeneration", nullable = false)
    var isRegeneration: Boolean = false,

    @Column(name = "regeneration_count", nullable = false)
    var regenerationCount: Int = 0,

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null
)
