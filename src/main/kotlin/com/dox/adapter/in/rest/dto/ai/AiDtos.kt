package com.dox.adapter.`in`.rest.dto.ai

import com.dox.domain.enum.AiGenerationStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class GenerateSectionRequest(
    val sectionType: String,
    val formResponseId: UUID? = null,
    val customerId: UUID? = null
)

data class GenerateSectionResponse(
    val text: String,
    val tokensUsed: Int,
    val model: String,
    val generationId: UUID,
    val cached: Boolean
)

data class AiUsageSummaryResponse(
    val used: Int,
    val limit: Int,
    val overage: Int,
    val overageCostCents: Int,
    val tierName: String?,
    val alertLevel: String?,
    val alertMessage: String?
)

data class AiUsageDetailResponse(
    val id: UUID,
    val reportId: UUID?,
    val generationId: UUID,
    val sectionType: String,
    val model: String,
    val inputTokens: Int,
    val outputTokens: Int,
    val estimatedCostBrl: BigDecimal,
    val status: AiGenerationStatus,
    val durationMs: Int,
    val isRegeneration: Boolean,
    val createdAt: LocalDateTime?
)

data class RegenerateSectionRequest(
    val sectionType: String,
    val generationId: UUID
)

data class AiQuotaResponse(
    val tier: String,
    val model: String,
    val monthlyLimit: Int,
    val overagePriceCents: Int,
    val enabled: Boolean
)

data class UpdateAiQuotaRequest(
    val aiTier: String? = null,
    val model: String? = null,
    val monthlyLimit: Int? = null,
    val overagePriceCents: Int? = null,
    val enabled: Boolean? = null
)

data class AiStatusResponse(
    val available: Boolean,
    val tierName: String?,
    val model: String?
)
