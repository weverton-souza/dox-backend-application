package com.dox.adapter.`in`.rest.dto.ai

import com.dox.domain.enum.AiGenerationStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class PreviousSectionInput(
    val sectionType: String,
    val summary: String
)

data class GenerateSectionRequest(
    @field:NotBlank(message = "Tipo da seção é obrigatório")
    @field:Size(max = 100, message = "Tipo da seção deve ter no máximo 100 caracteres")
    val sectionType: String,

    val formResponseId: UUID? = null,
    val customerId: UUID? = null,
    val previousSections: List<PreviousSectionInput>? = null,
    val quantitativeData: QuantitativeDataRequest? = null
)

data class GenerateSectionResponse(
    val text: String,
    val tokensUsed: Int,
    val model: String,
    val generationId: UUID,
    val cached: Boolean,
    val regenerationsUsed: Int = 0,
    val regenerationLimit: Int = 3
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

data class SectionInstructionRequest(
    val sectionTitle: String,
    val instruction: String? = null
)

data class GenerateFullReportRequest(
    val formResponseId: UUID? = null,
    val formResponseIds: List<UUID>? = null,
    val quantitativeData: QuantitativeDataRequest? = null,
    val quantitativeContext: String? = null,
    val selectedSections: List<SectionInstructionRequest>? = null,
    val includeCustomerData: Boolean = true
)

data class ReviewTextRequest(
    @field:NotBlank(message = "Texto é obrigatório")
    val text: String,

    @field:NotBlank(message = "Ação é obrigatória")
    @field:Size(max = 20, message = "Ação deve ter no máximo 20 caracteres")
    val action: String,

    @field:Size(max = 100, message = "Tipo da seção deve ter no máximo 100 caracteres")
    val sectionType: String? = null,

    @field:Size(max = 500, message = "Instrução deve ter no máximo 500 caracteres")
    val instruction: String? = null,

    val formResponseIds: List<UUID>? = null
)

data class ReviewTextResponse(
    val original: String,
    val revised: String,
    val generationId: UUID,
    val tokensUsed: Int,
    val model: String
)

data class AiGenerationSourceResponse(
    val id: UUID,
    val reportId: UUID,
    val generationId: UUID,
    val sourceType: String,
    val sourceId: UUID,
    val sourceLabel: String?,
    val included: Boolean,
    val displayOrder: Int,
    val createdAt: LocalDateTime?
)

data class QuantitativeDataRequest(
    val tables: List<ComputedTableDataRequest> = emptyList(),
    val charts: List<ComputedChartDataRequest> = emptyList()
)

data class ComputedTableDataRequest(
    val blockId: String,
    val title: String,
    val category: String = "",
    val dataStatus: String,
    val rows: List<ComputedTableRowRequest> = emptyList()
)

data class ComputedTableRowRequest(
    val label: String,
    val values: Map<String, String> = emptyMap()
)

data class ComputedChartDataRequest(
    val blockId: String,
    val title: String,
    val dataStatus: String,
    val series: List<ComputedChartSeriesRequest> = emptyList()
)

data class ComputedChartSeriesRequest(
    val label: String,
    val values: Map<String, Double> = emptyMap()
)

data class RegenerationInfoResponse(
    val used: Int,
    val limit: Int
)
