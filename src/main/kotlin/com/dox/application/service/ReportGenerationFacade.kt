package com.dox.application.service

import com.dox.application.port.input.AiStatus
import com.dox.application.port.input.AiUsageSummary
import com.dox.application.port.input.GenerateFullReportCommand
import com.dox.application.port.input.GenerateSectionCommand
import com.dox.application.port.input.GetAiUsageCommand
import com.dox.application.port.input.RegenerateSectionCommand
import com.dox.application.port.input.RegenerationInfo
import com.dox.application.port.input.ReportGenerationUseCase
import com.dox.application.port.input.ReviewTextCommand
import com.dox.application.port.input.SectionProgressEvent
import com.dox.application.port.input.UpdateAiQuotaCommand
import com.dox.domain.model.AiGenerationResult
import com.dox.domain.model.AiGenerationSource
import com.dox.domain.model.AiQuota
import com.dox.domain.model.AiUsage
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ReportGenerationFacade(
    private val aiGenerationService: AiGenerationService,
    private val fullReportGenerationService: FullReportGenerationService,
    private val aiUsageService: AiUsageService
) : ReportGenerationUseCase {
    override fun generateSection(command: GenerateSectionCommand): AiGenerationResult =
        aiGenerationService.generateSection(command)

    override fun regenerateSection(command: RegenerateSectionCommand): AiGenerationResult =
        aiGenerationService.regenerateSection(command)

    override fun reviewText(command: ReviewTextCommand): AiGenerationResult =
        aiGenerationService.reviewText(command)

    override fun generateFullReport(command: GenerateFullReportCommand, onSectionProgress: (SectionProgressEvent) -> Unit) =
        fullReportGenerationService.generateFullReport(command, onSectionProgress)

    override fun getUsageSummary(command: GetAiUsageCommand): AiUsageSummary =
        aiUsageService.getUsageSummary(command)

    override fun getUsageHistory(command: GetAiUsageCommand): List<AiUsage> =
        aiUsageService.getUsageHistory(command)

    override fun getUsageByReport(reportId: UUID): List<AiUsage> =
        aiUsageService.getUsageByReport(reportId)

    override fun getQuota(): AiQuota? =
        aiUsageService.getQuota()

    override fun updateQuota(command: UpdateAiQuotaCommand): AiQuota =
        aiUsageService.updateQuota(command)

    override fun getAiStatus(): AiStatus =
        aiUsageService.getAiStatus()

    override fun getGenerationSources(reportId: UUID): List<AiGenerationSource> =
        aiUsageService.getGenerationSources(reportId)

    override fun getRegenerationInfo(reportId: UUID): RegenerationInfo =
        aiUsageService.getRegenerationInfo(reportId)
}
