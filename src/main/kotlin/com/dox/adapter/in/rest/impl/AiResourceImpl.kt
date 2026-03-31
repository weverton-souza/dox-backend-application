package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.ai.AiGenerationSourceResponse
import com.dox.adapter.`in`.rest.dto.ai.AiQuotaResponse
import com.dox.adapter.`in`.rest.dto.ai.AiStatusResponse
import com.dox.adapter.`in`.rest.dto.ai.AiUsageDetailResponse
import com.dox.adapter.`in`.rest.dto.ai.AiUsageSummaryResponse
import com.dox.adapter.`in`.rest.dto.ai.GenerateFullReportRequest
import com.dox.adapter.`in`.rest.dto.ai.GenerateSectionRequest
import com.dox.adapter.`in`.rest.dto.ai.GenerateSectionResponse
import com.dox.adapter.`in`.rest.dto.ai.RegenerateSectionRequest
import com.dox.adapter.`in`.rest.dto.ai.RegenerationInfoResponse
import com.dox.adapter.`in`.rest.dto.ai.ReviewTextRequest
import com.dox.adapter.`in`.rest.dto.ai.ReviewTextResponse
import com.dox.adapter.`in`.rest.dto.ai.UpdateAiQuotaRequest
import com.dox.adapter.`in`.rest.resource.AiResource
import com.dox.application.port.input.ComputedChartData
import com.dox.application.port.input.ComputedChartSeries
import com.dox.application.port.input.ComputedTableData
import com.dox.application.port.input.ComputedTableRow
import com.dox.application.port.input.GenerateFullReportCommand
import com.dox.application.port.input.GenerateSectionCommand
import com.dox.application.port.input.GetAiUsageCommand
import com.dox.application.port.input.QuantitativeDataPayload
import com.dox.application.port.input.RegenerateSectionCommand
import com.dox.application.port.input.ReportGenerationUseCase
import com.dox.application.port.input.ReviewTextCommand
import com.dox.application.port.input.UpdateAiQuotaCommand
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.AiGenerationResult
import com.dox.domain.model.AiUsage
import com.dox.shared.ContextHolder
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.UUID
import java.util.concurrent.Executors

@RestController
class AiResourceImpl(
    private val reportGenerationUseCase: ReportGenerationUseCase,
    private val objectMapper: ObjectMapper
) : AiResource {
    private val log = LoggerFactory.getLogger(javaClass)
    private val sseExecutor = Executors.newCachedThreadPool()

    override fun generateFullReport(id: UUID, request: GenerateFullReportRequest): SseEmitter {
        val emitter = SseEmitter(300_000L)

        val userId = ContextHolder.getUserIdOrThrow()
        val tenantId = ContextHolder.getTenantIdOrThrow()

        val quantitativeData = request.quantitativeData?.let { qd ->
            QuantitativeDataPayload(
                tables = qd.tables.map { t ->
                    ComputedTableData(
                        t.blockId,
                        t.title,
                        t.category,
                        t.dataStatus,
                        t.rows.map { r -> ComputedTableRow(r.label, r.values) }
                    )
                },
                charts = qd.charts.map { c ->
                    ComputedChartData(
                        c.blockId,
                        c.title,
                        c.dataStatus,
                        c.series.map { s -> ComputedChartSeries(s.label, s.values) }
                    )
                }
            )
        }

        val resolvedFormResponseIds = request.formResponseIds ?: emptyList()

        val selectedSectionTitles = request.selectedSections?.map { it.sectionTitle }
        val sectionInstructions = request.selectedSections
            ?.filter { !it.instruction.isNullOrBlank() }
            ?.associate { it.sectionTitle to it.instruction }
            ?: emptyMap()

        val command = GenerateFullReportCommand(
            reportId = id,
            formResponseIds = resolvedFormResponseIds,
            includeCustomerData = request.includeCustomerData,
            quantitativeData = quantitativeData,
            quantitativeContext = request.quantitativeContext,
            selectedSections = selectedSectionTitles,
            sectionInstructions = sectionInstructions
        )

        sseExecutor.submit {
            try {
                ContextHolder.setUserId(userId)
                ContextHolder.setTenantId(tenantId)
                com.dox.adapter.out.tenant.TenantContext.setTenantId(
                    com.dox.adapter.out.tenant.TenantContext.convertToSchemaName(tenantId.toString())
                )

                reportGenerationUseCase.generateFullReport(command) { event ->
                    try {
                        val eventName = if (event.status == "done") "generation-complete" else "section-progress"
                        emitter.send(
                            SseEmitter.event()
                                .name(eventName)
                                .data(objectMapper.writeValueAsString(event))
                        )
                    } catch (e: Exception) {
                        log.warn("Failed to send SSE event: {}", e.message)
                    }
                }

                emitter.complete()
            } catch (e: Exception) {
                log.error("Full report generation failed: {}", e.message)
                try {
                    emitter.send(
                        SseEmitter.event()
                            .name("error")
                            .data(objectMapper.writeValueAsString(mapOf("message" to (e.message ?: "Erro interno"))))
                    )
                } catch (_: Exception) {
                }
                emitter.completeWithError(e)
            } finally {
                ContextHolder.clear()
            }
        }

        emitter.onTimeout { emitter.complete() }
        emitter.onError { emitter.complete() }

        return emitter
    }

    override fun generateSection(id: UUID, request: GenerateSectionRequest): ResponseEntity<GenerateSectionResponse> {
        val result = reportGenerationUseCase.generateSection(
            GenerateSectionCommand(
                reportId = id,
                sectionType = request.sectionType,
                formResponseId = request.formResponseId,
                previousSections = request.previousSections?.map {
                    com.dox.application.port.input.PreviousSectionContext(it.sectionType, it.summary)
                },
                quantitativeData = request.quantitativeData?.let { qd ->
                    QuantitativeDataPayload(
                        tables = qd.tables.map { t ->
                            ComputedTableData(
                                t.blockId,
                                t.title,
                                t.category,
                                t.dataStatus,
                                t.rows.map { r -> ComputedTableRow(r.label, r.values) }
                            )
                        },
                        charts = qd.charts.map { c ->
                            ComputedChartData(
                                c.blockId,
                                c.title,
                                c.dataStatus,
                                c.series.map { s -> ComputedChartSeries(s.label, s.values) }
                            )
                        }
                    )
                }
            )
        )
        val regenInfo = reportGenerationUseCase.getRegenerationInfo(id)
        return responseEntity(result.toResponse(regenInfo.used, regenInfo.limit))
    }

    override fun regenerateSection(id: UUID, request: RegenerateSectionRequest): ResponseEntity<GenerateSectionResponse> {
        val result = reportGenerationUseCase.regenerateSection(
            RegenerateSectionCommand(
                reportId = id,
                sectionType = request.sectionType
            )
        )
        val regenInfo = reportGenerationUseCase.getRegenerationInfo(id)
        return responseEntity(result.toResponse(regenInfo.used, regenInfo.limit))
    }

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
            ?: throw ResourceNotFoundException("Quota do Assistente", "tenant")
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

    override fun reviewText(id: UUID, request: ReviewTextRequest): ResponseEntity<ReviewTextResponse> {
        val result = reportGenerationUseCase.reviewText(
            ReviewTextCommand(
                reportId = id,
                text = request.text,
                action = request.action,
                sectionType = request.sectionType,
                instruction = request.instruction,
                formResponseIds = request.formResponseIds
            )
        )
        return responseEntity(
            ReviewTextResponse(
                original = request.text,
                revised = result.text,
                generationId = result.generationId,
                tokensUsed = result.inputTokens + result.outputTokens,
                model = result.model
            )
        )
    }

    override fun getGenerationSources(reportId: UUID): ResponseEntity<List<AiGenerationSourceResponse>> =
        responseEntity(
            reportGenerationUseCase.getGenerationSources(reportId).map {
                AiGenerationSourceResponse(
                    id = it.id,
                    reportId = it.reportId,
                    generationId = it.generationId,
                    sourceType = it.sourceType,
                    sourceId = it.sourceId,
                    sourceLabel = it.sourceLabel,
                    included = it.included,
                    displayOrder = it.displayOrder,
                    createdAt = it.createdAt
                )
            }
        )

    override fun getRegenerationInfo(reportId: UUID): ResponseEntity<RegenerationInfoResponse> {
        val info = reportGenerationUseCase.getRegenerationInfo(reportId)
        return responseEntity(RegenerationInfoResponse(used = info.used, limit = info.limit))
    }

    private fun AiGenerationResult.toResponse(
        regenerationsUsed: Int = 0,
        regenerationLimit: Int = 3
    ) = GenerateSectionResponse(
        text = text,
        tokensUsed = inputTokens + outputTokens,
        model = model,
        generationId = generationId,
        cached = cached,
        regenerationsUsed = regenerationsUsed,
        regenerationLimit = regenerationLimit
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
