package com.dox.domain.model

import com.dox.domain.enum.AiGenerationStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class AiUsage(
    val id: UUID = UUID.randomUUID(),
    val reportId: UUID? = null,
    val generationId: UUID = UUID.randomUUID(),
    val professionalId: UUID,
    val sectionType: String,
    val model: String,
    val inputTokens: Int = 0,
    val outputTokens: Int = 0,
    val cacheReadTokens: Int = 0,
    val cacheWriteTokens: Int = 0,
    val estimatedCostBrl: BigDecimal = BigDecimal.ZERO,
    val status: AiGenerationStatus = AiGenerationStatus.SUCCESS,
    val errorMessage: String? = null,
    val durationMs: Int = 0,
    val isRegeneration: Boolean = false,
    val regenerationCount: Int = 0,
    val createdAt: LocalDateTime? = null,
)

data class TokenSummary(
    val totalInputTokens: Long,
    val totalOutputTokens: Long,
    val totalCacheReadTokens: Long,
    val totalCacheWriteTokens: Long,
    val totalCostBrl: BigDecimal,
)
