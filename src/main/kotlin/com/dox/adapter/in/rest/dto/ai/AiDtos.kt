package com.dox.adapter.`in`.rest.dto.ai

import com.dox.domain.enum.AiGenerationStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class GenerateSectionRequest(
    @field:NotBlank(message = "Tipo da seção é obrigatório")
    @field:Size(max = 100, message = "Tipo da seção deve ter no máximo 100 caracteres")
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
    @field:NotBlank(message = "Tipo da seção é obrigatório")
    @field:Size(max = 100, message = "Tipo da seção deve ter no máximo 100 caracteres")
    val sectionType: String,

    @field:NotNull(message = "ID da geração é obrigatório")
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
    @field:Size(max = 100, message = "Tier deve ter no máximo 100 caracteres")
    val aiTier: String? = null,

    @field:Size(max = 100, message = "Modelo deve ter no máximo 100 caracteres")
    val model: String? = null,

    @field:Min(value = 0, message = "Limite mensal não pode ser negativo")
    val monthlyLimit: Int? = null,

    @field:Min(value = 0, message = "Preço de excedente não pode ser negativo")
    val overagePriceCents: Int? = null,

    val enabled: Boolean? = null
)

data class AiStatusResponse(
    val available: Boolean,
    val tierName: String?,
    val model: String?
)
