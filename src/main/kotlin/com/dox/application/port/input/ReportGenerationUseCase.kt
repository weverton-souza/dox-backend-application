package com.dox.application.port.input

import com.dox.domain.model.AiGenerationResult
import com.dox.domain.model.AiGenerationSource
import com.dox.domain.model.AiQuota
import com.dox.domain.model.AiUsage
import java.util.UUID

data class PreviousSectionContext(
    val sectionType: String,
    val summary: String
)

data class GenerateSectionCommand(
    val reportId: UUID,
    val sectionType: String,
    val formResponseId: UUID? = null,
    val formResponseIds: List<UUID>? = null,
    val previousSections: List<PreviousSectionContext>? = null,
    val quantitativeData: QuantitativeDataPayload? = null
)

data class RegenerateSectionCommand(
    val reportId: UUID,
    val sectionType: String
)

data class GenerateFullReportCommand(
    val reportId: UUID,
    val formResponseId: UUID? = null,
    val formResponseIds: List<UUID>? = null,
    val quantitativeData: QuantitativeDataPayload? = null,
    val selectedSections: List<String>? = null
)

data class QuantitativeDataPayload(
    val tables: List<ComputedTableData> = emptyList(),
    val charts: List<ComputedChartData> = emptyList()
)

data class ComputedTableData(
    val blockId: String,
    val title: String,
    val category: String,
    val dataStatus: String,
    val rows: List<ComputedTableRow> = emptyList()
)

data class ComputedTableRow(
    val label: String,
    val values: Map<String, String> = emptyMap()
)

data class ComputedChartData(
    val blockId: String,
    val title: String,
    val dataStatus: String,
    val series: List<ComputedChartSeries> = emptyList()
)

data class ComputedChartSeries(
    val label: String,
    val values: Map<String, Double> = emptyMap()
)

data class GetAiUsageCommand(
    val month: Int,
    val year: Int
)

data class UpdateAiQuotaCommand(
    val aiTier: String? = null,
    val model: String? = null,
    val monthlyLimit: Int? = null,
    val overagePriceCents: Int? = null,
    val enabled: Boolean? = null
)

data class AiStatus(
    val available: Boolean,
    val tierName: String?,
    val model: String?
)

interface ReportGenerationUseCase {

    fun generateSection(command: GenerateSectionCommand): AiGenerationResult

    fun regenerateSection(command: RegenerateSectionCommand): AiGenerationResult

    fun generateFullReport(command: GenerateFullReportCommand, onSectionProgress: (SectionProgressEvent) -> Unit)

    fun getUsageSummary(command: GetAiUsageCommand): AiUsageSummary

    fun getUsageHistory(command: GetAiUsageCommand): List<AiUsage>

    fun getUsageByReport(reportId: UUID): List<AiUsage>

    fun getQuota(): AiQuota?

    fun updateQuota(command: UpdateAiQuotaCommand): AiQuota

    fun getAiStatus(): AiStatus

    fun getGenerationSources(reportId: UUID): List<AiGenerationSource>
}

data class AiUsageSummary(
    val used: Int,
    val limit: Int,
    val overage: Int,
    val overageCostCents: Int,
    val quota: AiQuota?,
    val alertLevel: AlertLevel? = null
)

data class SectionProgressEvent(
    val sectionType: String,
    val index: Int,
    val total: Int,
    val status: String,
    val text: String? = null,
    val generationId: String? = null,
    val tokensUsed: Int? = null,
    val message: String? = null,
    val warning: String? = null
)

data class SectionPlan(
    val title: String,
    val status: String = "full",
    val relevantData: List<String> = emptyList(),
    val missingData: List<String> = emptyList(),
    val warning: String? = null
)

data class GenerationPlan(
    val verticalContext: String = "",
    val sections: List<SectionPlan> = emptyList()
)

data class GenerationCompleteEvent(
    val completedCount: Int,
    val failedCount: Int,
    val totalTokens: Int,
    val totalCostBrl: String
)

enum class AlertLevel(val message: String) {
    WARNING_80("Você já usou 80% da sua franquia de laudos com IA"),
    LIMIT_REACHED("Franquia mensal atingida. Gerações adicionais serão cobradas como excedente"),
    OVERAGE("Você está usando laudos excedentes que serão cobrados no fechamento")
}
