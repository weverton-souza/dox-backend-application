package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.ai.AiQuotaResponse
import com.dox.adapter.`in`.rest.dto.ai.AiStatusResponse
import com.dox.adapter.`in`.rest.dto.ai.AiUsageDetailResponse
import com.dox.adapter.`in`.rest.dto.ai.AiUsageSummaryResponse
import com.dox.adapter.`in`.rest.dto.ai.GenerateSectionRequest
import com.dox.adapter.`in`.rest.dto.ai.GenerateSectionResponse
import com.dox.adapter.`in`.rest.dto.ai.RegenerateSectionRequest
import com.dox.adapter.`in`.rest.dto.ai.UpdateAiQuotaRequest
import com.dox.adapter.`in`.rest.resource.AiResource
import com.dox.application.port.input.GenerateSectionCommand
import com.dox.application.port.input.GetAiUsageCommand
import com.dox.application.port.input.RegenerateSectionCommand
import com.dox.application.port.input.ReportGenerationUseCase
import com.dox.application.port.input.UpdateAiQuotaCommand
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.AiGenerationResult
import com.dox.domain.model.AiUsage
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class AiResourceImpl(
    private val reportGenerationUseCase: ReportGenerationUseCase
) : AiResource {

    override fun generateSection(id: UUID, request: GenerateSectionRequest): ResponseEntity<GenerateSectionResponse> =
        responseEntity(
            reportGenerationUseCase.generateSection(
                GenerateSectionCommand(
                    reportId = id,
                    sectionType = request.sectionType,
                    formResponseId = request.formResponseId
                )
            ).toResponse()
        )

    override fun regenerateSection(id: UUID, request: RegenerateSectionRequest): ResponseEntity<GenerateSectionResponse> =
        responseEntity(
            reportGenerationUseCase.regenerateSection(
                RegenerateSectionCommand(
                    reportId = id,
                    sectionType = request.sectionType
                )
            ).toResponse()
        )

    override fun getUsageSummary(month: Int, year: Int): ResponseEntity<AiUsageSummaryResponse> =
        responseEntity(
            reportGenerationUseCase.getUsageSummary(GetAiUsageCommand(month, year)).let { summary ->
                AiUsageSummaryResponse(
                    used = summary.used,
                    limit = summary.limit,
                    overage = summary.overage,
                    overageCostCents = summary.overageCostCents,
                    tierName = summary.quota?.aiTier?.name,
                    alertLevel = summary.alertLevel?.name,
                    alertMessage = summary.alertLevel?.message
                )
            }
        )

    override fun getUsageHistory(month: Int, year: Int): ResponseEntity<List<AiUsageDetailResponse>> =
        responseEntity(
            reportGenerationUseCase.getUsageHistory(GetAiUsageCommand(month, year)).map { it.toResponse() }
        )

    override fun getUsageByReport(reportId: UUID): ResponseEntity<List<AiUsageDetailResponse>> =
        responseEntity(
            reportGenerationUseCase.getUsageByReport(reportId).map { it.toResponse() }
        )

    override fun getQuota(): ResponseEntity<AiQuotaResponse> {
        val quota = reportGenerationUseCase.getQuota()
            ?: throw ResourceNotFoundException("Quota IA", "tenant")
        return responseEntity(
            AiQuotaResponse(
                tier = quota.aiTier.name,
                model = quota.model,
                monthlyLimit = quota.monthlyLimit,
                overagePriceCents = quota.overagePriceCents,
                enabled = quota.enabled
            )
        )
    }

    override fun updateQuota(request: UpdateAiQuotaRequest): ResponseEntity<AiQuotaResponse> {
        val quota = reportGenerationUseCase.updateQuota(
            UpdateAiQuotaCommand(
                aiTier = request.aiTier,
                model = request.model,
                monthlyLimit = request.monthlyLimit,
                overagePriceCents = request.overagePriceCents,
                enabled = request.enabled
            )
        )
        return responseEntity(
            AiQuotaResponse(
                tier = quota.aiTier.name,
                model = quota.model,
                monthlyLimit = quota.monthlyLimit,
                overagePriceCents = quota.overagePriceCents,
                enabled = quota.enabled
            )
        )
    }

    override fun getAiStatus(): ResponseEntity<AiStatusResponse> {
        val status = reportGenerationUseCase.getAiStatus()
        return responseEntity(
            AiStatusResponse(
                available = status.available,
                tierName = status.tierName,
                model = status.model
            )
        )
    }

    private fun AiGenerationResult.toResponse() = GenerateSectionResponse(
        text = text,
        tokensUsed = inputTokens + outputTokens,
        model = model,
        generationId = generationId,
        cached = cached
    )

    private fun AiUsage.toResponse() = AiUsageDetailResponse(
        id = id,
        reportId = reportId,
        generationId = generationId,
        sectionType = sectionType,
        model = model,
        inputTokens = inputTokens,
        outputTokens = outputTokens,
        estimatedCostBrl = estimatedCostBrl,
        status = status,
        durationMs = durationMs,
        isRegeneration = isRegeneration,
        createdAt = createdAt
    )
}
