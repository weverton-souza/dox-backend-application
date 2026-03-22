package com.dox.application.port.input

import com.dox.domain.model.AiGenerationResult
import com.dox.domain.model.AiQuota
import com.dox.domain.model.AiUsage
import java.util.UUID

data class GenerateSectionCommand(
    val reportId: UUID,
    val sectionType: String,
    val formResponseId: UUID? = null,
    val customerId: UUID? = null
)

data class RegenerateSectionCommand(
    val reportId: UUID,
    val sectionType: String,
    val generationId: UUID
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

    fun getUsageSummary(command: GetAiUsageCommand): AiUsageSummary

    fun getUsageHistory(command: GetAiUsageCommand): List<AiUsage>

    fun getUsageByReport(reportId: UUID): List<AiUsage>

    fun getQuota(): AiQuota?

    fun updateQuota(command: UpdateAiQuotaCommand): AiQuota

    fun getAiStatus(): AiStatus
}

data class AiUsageSummary(
    val used: Int,
    val limit: Int,
    val overage: Int,
    val overageCostCents: Int,
    val quota: AiQuota?,
    val alertLevel: AlertLevel? = null
)

enum class AlertLevel(val message: String) {
    WARNING_80("Você já usou 80% da sua franquia de laudos com IA"),
    LIMIT_REACHED("Franquia mensal atingida. Gerações adicionais serão cobradas como excedente"),
    OVERAGE("Você está usando laudos excedentes que serão cobrados no fechamento")
}
