package com.dox.application.service

import com.dox.application.port.input.GenerateFullReportCommand
import com.dox.application.port.input.GenerateSectionCommand
import com.dox.application.port.input.GenerationCompleteEvent
import com.dox.application.port.input.GenerationPlan
import com.dox.application.port.input.PreviousSectionContext
import com.dox.application.port.input.SectionPlan
import com.dox.application.port.input.SectionProgressEvent
import com.dox.application.port.output.AiConfigPort
import com.dox.application.port.output.AiGenerationPort
import com.dox.application.port.output.AiGenerationSourcePersistencePort
import com.dox.application.port.output.AiPlanningParserPort
import com.dox.application.port.output.AiSystemPromptPort
import com.dox.application.port.output.FormPersistencePort
import com.dox.application.port.output.ReportPersistencePort
import com.dox.application.port.output.TenantPersistencePort
import com.dox.domain.enum.Vertical
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.AiGenerationSource
import com.dox.shared.ContextHolder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID

@Service
class FullReportGenerationService(
    private val aiGenerationService: AiGenerationService,
    private val reportBlockService: ReportBlockService,
    private val aiGenerationPort: AiGenerationPort,
    private val aiGenerationSourcePort: AiGenerationSourcePersistencePort,
    private val reportPersistencePort: ReportPersistencePort,
    private val formPersistencePort: FormPersistencePort,
    private val tenantPersistencePort: TenantPersistencePort,
    private val systemPromptPort: AiSystemPromptPort,
    private val planningParserPort: AiPlanningParserPort,
    private val aiConfigPort: AiConfigPort
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun generateFullReport(
        command: GenerateFullReportCommand,
        onSectionProgress: (SectionProgressEvent) -> Unit
    ) {
        val userId = ContextHolder.getUserIdOrThrow()
        val tenantId = ContextHolder.getTenantIdOrThrow()

        val (quota, semaphore) = aiGenerationService.validateAndAcquireQuota(tenantId, command.reportId)

        try {
            val report = reportPersistencePort.findById(command.reportId)
                ?: throw ResourceNotFoundException("Relatório", command.reportId.toString())

            val resolvedFormResponseIds = command.formResponseIds ?: emptyList()

            val fillableBlocks = report.blocks
                .filter { block ->
                    val type = block["type"]?.toString()
                    type == "section" || type == "info-box"
                }
                .sortedBy { (it["order"] as? Number)?.toInt() ?: 0 }

            if (fillableBlocks.isEmpty()) {
                throw BusinessException("Nenhum bloco de seção ou info-box encontrado no relatório")
            }

            val blocksToGenerate = if (command.selectedSections != null) {
                fillableBlocks.filter { reportBlockService.extractSectionType(it) in command.selectedSections }
            } else {
                fillableBlocks
            }

            if (blocksToGenerate.isEmpty()) {
                throw BusinessException("Nenhuma seção selecionada para geração")
            }

            val total = blocksToGenerate.size
            val previousSections = mutableListOf<PreviousSectionContext>()
            var completedCount = 0
            var failedCount = 0
            var skippedCount = 0
            var totalTokens = 0
            var totalCostBrl = BigDecimal.ZERO

            val tenant = tenantPersistencePort.findById(tenantId)
                ?: throw ResourceNotFoundException("Tenant", tenantId.toString())

            val sectionTitles = blocksToGenerate.map { reportBlockService.extractSectionType(it) }
            val plan = buildGenerationPlan(tenant.vertical, sectionTitles, command, quota.model)
            val planMap = plan.sections.associateBy { it.title }

            log.info("Generation plan: {} sections — full={}, partial={}, skip={}",
                sectionTitles.size,
                plan.sections.count { it.status == "full" },
                plan.sections.count { it.status == "partial" },
                plan.sections.count { it.status == "skip" }
            )

            for ((index, block) in blocksToGenerate.withIndex()) {
                val sectionType = reportBlockService.extractSectionType(block)
                val sectionPlan = planMap[sectionType] ?: SectionPlan(title = sectionType, status = "full")

                if (sectionPlan.status == "skip") {
                    skippedCount++
                    val warningMessage = sectionPlan.warning ?: "Dados insuficientes para gerar esta seção"
                    applyBlockContent(command.reportId, block, warningMessage, skipped = true)
                    onSectionProgress(SectionProgressEvent(
                        sectionType = sectionType, index = index + 1, total = total,
                        status = "skipped", message = warningMessage
                    ))
                    continue
                }

                try {
                    val sectionCommand = GenerateSectionCommand(
                        reportId = command.reportId,
                        sectionType = sectionType,
                        formResponseIds = resolvedFormResponseIds,
                        previousSections = previousSections.toList(),
                        quantitativeData = command.quantitativeData,
                        quantitativeContext = command.quantitativeContext,
                        instruction = command.sectionInstructions[sectionType],
                        includeCustomerData = command.includeCustomerData
                    )

                    val (systemPrompt, userPrompt) = aiGenerationService.buildGenerationContext(
                        command.reportId, sectionCommand, resolvedFormResponseIds
                    )

                    val finalUserPrompt = if (sectionPlan.status == "partial" && sectionPlan.warning != null) {
                        "$userPrompt\n\nATENÇÃO: ${sectionPlan.warning}. Gere o texto com base nos dados disponíveis e indique claramente onde dados complementares seriam necessários."
                    } else {
                        userPrompt
                    }

                    val result = aiGenerationService.executeWithRetry(systemPrompt, finalUserPrompt, quota.model)
                    val sanitizedText = aiGenerationService.sanitizeOutput(result.text)

                    if (sanitizedText.startsWith("[DADOS_INSUFICIENTES]")) {
                        skippedCount++
                        val warningMessage = sanitizedText.removePrefix("[DADOS_INSUFICIENTES]:").trim()
                        applyBlockContent(command.reportId, block, warningMessage, skipped = true)
                        onSectionProgress(SectionProgressEvent(
                            sectionType = sectionType, index = index + 1, total = total,
                            status = "skipped", message = warningMessage
                        ))
                        continue
                    }

                    val insufficientIdx = sanitizedText.indexOf("[DADOS_INSUFICIENTES]")
                    val cleanedText = if (insufficientIdx >= 0) {
                        sanitizedText.substring(0, insufficientIdx).trim()
                    } else {
                        sanitizedText
                    }

                    val costBrl = aiGenerationService.calculateCost(result)

                    aiGenerationService.recordUsage(
                        reportId = command.reportId,
                        userId = userId,
                        sectionType = sectionType,
                        result = result,
                        costBrl = costBrl
                    )

                    applyBlockContent(command.reportId, block, cleanedText)

                    previousSections.add(
                        PreviousSectionContext(sectionType, summarizeForContext(sectionType, cleanedText))
                    )

                    totalTokens += result.inputTokens + result.outputTokens
                    totalCostBrl = totalCostBrl.add(costBrl)
                    completedCount++

                    onSectionProgress(SectionProgressEvent(
                        sectionType = sectionType, index = index + 1, total = total,
                        status = "completed", text = cleanedText,
                        generationId = result.generationId.toString(),
                        tokensUsed = result.inputTokens + result.outputTokens,
                        warning = sectionPlan.warning
                    ))
                } catch (e: Exception) {
                    if (e is BusinessException && e.message?.contains("Plano do Assistente") == true) throw e

                    log.error("Failed to generate section '{}' for report {}: {}", sectionType, command.reportId, e.message)
                    failedCount++

                    aiGenerationService.recordFailure(
                        reportId = command.reportId,
                        userId = userId,
                        sectionType = sectionType,
                        model = quota.model,
                        errorMessage = e.message?.take(500)
                    )

                    onSectionProgress(SectionProgressEvent(
                        sectionType = sectionType, index = index + 1, total = total,
                        status = "error", message = e.message?.take(200) ?: "Erro desconhecido"
                    ))
                }
            }

            if (resolvedFormResponseIds.isNotEmpty() && completedCount > 0) {
                val generationId = UUID.randomUUID()
                val sources = resolvedFormResponseIds.mapIndexed { index, responseId ->
                    AiGenerationSource(
                        reportId = command.reportId,
                        generationId = generationId,
                        sourceType = "form_response",
                        sourceId = responseId,
                        displayOrder = index
                    )
                }
                aiGenerationSourcePort.saveAll(sources)
            }

            onSectionProgress(SectionProgressEvent(
                sectionType = "_complete", index = total, total = total, status = "done",
                message = GenerationCompleteEvent(
                    completedCount = completedCount,
                    failedCount = failedCount,
                    totalTokens = totalTokens,
                    totalCostBrl = totalCostBrl.setScale(4, RoundingMode.HALF_UP).toPlainString()
                ).toString()
            ))
        } finally {
            semaphore.release()
        }
    }

    private fun applyBlockContent(reportId: UUID, block: Map<String, Any?>, text: String, skipped: Boolean = false) {
        val blockType = block["type"]?.toString()
        if (blockType == "info-box") {
            reportBlockService.updateBlockContent(reportId, block, text, skipped)
        } else {
            reportBlockService.insertContentBlockAfter(reportId, block, text, skipped)
        }
    }

    private fun buildGenerationPlan(
        vertical: Vertical,
        sectionTitles: List<String>,
        command: GenerateFullReportCommand,
        model: String
    ): GenerationPlan {
        try {
            val report = reportPersistencePort.findById(command.reportId) ?: return defaultPlan(sectionTitles)
            val firstFormResponseId = command.formResponseIds?.firstOrNull() ?: report.formResponseId
            val formResponse = firstFormResponseId?.let { formPersistencePort.findResponseById(it) }

            val dataSummary = buildDataSummary(formResponse, command)
            val titlesText = sectionTitles.mapIndexed { i, t -> "${i + 1}. $t" }.joinToString("\n")

            val planningPrompt = systemPromptPort.buildPlanningPrompt(vertical, titlesText, dataSummary)
                ?: return defaultPlan(sectionTitles)

            val systemPrompt = systemPromptPort.build(vertical)
            val result = aiGenerationPort.generateSection(systemPrompt, planningPrompt, model, maxTokens = 2000)

            return planningParserPort.parse(result.text, sectionTitles)
        } catch (e: Exception) {
            log.warn("Planning call failed, using default plan: {}", e.message)
            return defaultPlan(sectionTitles)
        }
    }

    private fun buildDataSummary(formResponse: com.dox.domain.model.FormResponse?, command: GenerateFullReportCommand): String {
        val parts = mutableListOf<String>()

        if (formResponse != null && formResponse.answers.isNotEmpty()) {
            val answersText = formResponse.answers.joinToString("\n") { answer ->
                val label = answer["label"]?.toString() ?: answer["fieldId"]?.toString() ?: ""
                val value = answer["value"]?.toString() ?: ""
                "- $label: $value"
            }
            parts.add("### Respostas do questionário (${formResponse.answers.size} campos)\n$answersText")
        } else {
            parts.add("### Respostas do questionário\nNenhuma resposta disponível.")
        }

        command.quantitativeData?.let { qd ->
            val filledTables = qd.tables.filter { it.dataStatus != "empty" }
            val filledCharts = qd.charts.filter { it.dataStatus != "empty" }

            if (filledTables.isNotEmpty() || filledCharts.isNotEmpty()) {
                val tablesSummary = filledTables.joinToString("\n") { "- Tabela: ${it.title} (${it.category}) [${it.dataStatus}]" }
                val chartsSummary = filledCharts.joinToString("\n") { "- Gráfico: ${it.title} [${it.dataStatus}]" }
                parts.add("### Dados quantitativos\n$tablesSummary\n$chartsSummary")
            } else {
                parts.add("### Dados quantitativos\nNenhum dado quantitativo disponível.")
            }
        } ?: parts.add("### Dados quantitativos\nNenhum dado quantitativo disponível.")

        return parts.joinToString("\n\n")
    }

    private fun defaultPlan(sectionTitles: List<String>): GenerationPlan =
        GenerationPlan(
            verticalContext = "",
            sections = sectionTitles.map { SectionPlan(title = it, status = "full") }
        )

    private fun summarizeForContext(sectionType: String, text: String): String {
        val maxChars = 800
        if (text.length <= maxChars) return text

        val truncated = text.take(maxChars)
        val lastSentenceEnd = truncated.lastIndexOfAny(charArrayOf('.', '!', '?'))
        return if (lastSentenceEnd > maxChars / 2) {
            truncated.substring(0, lastSentenceEnd + 1)
        } else {
            "$truncated..."
        }
    }
}
