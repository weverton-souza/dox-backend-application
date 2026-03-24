package com.dox.application.service

import com.dox.adapter.out.ai.config.AiConfig
import com.dox.adapter.out.ai.prompt.PlanningResponseParser
import com.dox.adapter.out.ai.prompt.SectionPromptBuilder
import com.dox.adapter.out.ai.prompt.SystemPromptBuilder
import com.dox.application.port.input.AiStatus
import com.dox.application.port.input.AiUsageSummary
import com.dox.application.port.input.AlertLevel
import com.dox.application.port.input.GenerateFullReportCommand
import com.dox.application.port.input.GenerateSectionCommand
import com.dox.application.port.input.GenerationCompleteEvent
import com.dox.application.port.input.GenerationPlan
import com.dox.application.port.input.GetAiUsageCommand
import com.dox.application.port.input.PreviousSectionContext
import com.dox.application.port.input.RegenerateSectionCommand
import com.dox.application.port.input.ReportGenerationUseCase
import com.dox.application.port.input.SectionPlan
import com.dox.application.port.input.SectionProgressEvent
import com.dox.application.port.input.UpdateAiQuotaCommand
import com.dox.application.port.output.AiGenerationPort
import com.dox.application.port.output.AiGenerationSourcePersistencePort
import com.dox.application.port.output.AiQuotaPort
import com.dox.application.port.output.AiUsagePort
import com.dox.application.port.output.CustomerPersistencePort
import com.dox.application.port.output.FormPersistencePort
import com.dox.application.port.output.ReportPersistencePort
import com.dox.application.port.output.TenantPersistencePort
import com.dox.domain.enum.Vertical
import com.dox.domain.enum.AiGenerationStatus
import com.dox.domain.enum.AiTier
import com.dox.domain.model.AiQuota
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.AiGenerationResult
import com.dox.domain.model.AiGenerationSource
import com.dox.domain.model.AiUsage
import com.dox.shared.ContextHolder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore

@Service
class ReportGenerationServiceImpl(
    private val aiGenerationPort: AiGenerationPort,
    private val aiUsagePort: AiUsagePort,
    private val aiQuotaPort: AiQuotaPort,
    private val aiGenerationSourcePort: AiGenerationSourcePersistencePort,
    private val reportPersistencePort: ReportPersistencePort,
    private val customerPersistencePort: CustomerPersistencePort,
    private val formPersistencePort: FormPersistencePort,
    private val tenantPersistencePort: TenantPersistencePort,
    private val professionalPersistencePort: com.dox.application.port.output.ProfessionalSettingsPersistencePort,
    private val templatePersistencePort: com.dox.application.port.output.TemplatePersistencePort,
    private val systemPromptBuilder: SystemPromptBuilder,
    private val sectionPromptBuilder: SectionPromptBuilder,
    private val planningResponseParser: PlanningResponseParser,
    private val aiConfig: AiConfig
) : ReportGenerationUseCase {

    private val log = LoggerFactory.getLogger(javaClass)
    private val tenantSemaphores = ConcurrentHashMap<String, Semaphore>()
    private companion object {
        private const val MAX_SEMAPHORE_CACHE_SIZE = 1000
    }

    @Transactional
    override fun generateSection(command: GenerateSectionCommand): AiGenerationResult {
        val userId = ContextHolder.getUserIdOrThrow()
        val tenantId = ContextHolder.getTenantIdOrThrow()

        val (quota, semaphore) = validateAndAcquireQuota(tenantId, command.reportId)

        try {
            val (systemPrompt, userPrompt) = buildGenerationContext(command.reportId, command)

            val result = executeAiGeneration(systemPrompt, userPrompt, quota.model)

            val sanitizedText = sanitizeOutput(result.text)
            val costBrl = calculateCost(result)

            recordUsage(
                reportId = command.reportId,
                userId = userId,
                sectionType = command.sectionType,
                result = result,
                costBrl = costBrl
            )

            return result.copy(text = sanitizedText)
        } catch (e: Exception) {
            if (e is BusinessException || e is ResourceNotFoundException) throw e

            log.error("AI generation failed for report {}: {}", command.reportId, e.message)

            recordFailure(
                reportId = command.reportId,
                userId = userId,
                sectionType = command.sectionType,
                model = quota.model,
                errorMessage = e.message?.take(500)
            )

            throw BusinessException("Erro ao gerar seção com IA. Tente novamente.")
        } finally {
            semaphore.release()
        }
    }

    private fun validateAndAcquireQuota(tenantId: UUID, reportId: UUID): Pair<AiQuota, Semaphore> {
        val quota = aiQuotaPort.findQuota()
            ?: throw BusinessException("IA não habilitada para este workspace")

        if (!quota.enabled || quota.aiTier == AiTier.NONE) {
            throw BusinessException("Plano de IA não ativo")
        }

        reportPersistencePort.findById(reportId)
            ?: throw ResourceNotFoundException("Relatório", reportId.toString())

        val semaphore = tenantSemaphores.computeIfAbsent(tenantId.toString()) {
            Semaphore(aiConfig.maxConcurrentPerTenant)
        }

        if (tenantSemaphores.size > MAX_SEMAPHORE_CACHE_SIZE) {
            val keysToRemove = tenantSemaphores.keys()
                .asSequence()
                .filter { it != tenantId.toString() }
                .take(tenantSemaphores.size - MAX_SEMAPHORE_CACHE_SIZE)
                .toList()
            keysToRemove.forEach { tenantSemaphores.remove(it) }
        }

        if (!semaphore.tryAcquire()) {
            throw BusinessException("Limite de gerações simultâneas atingido. Tente novamente em instantes.")
        }

        return Pair(quota, semaphore)
    }

    private fun buildGenerationContext(reportId: UUID, command: GenerateSectionCommand, formResponseIds: List<UUID>? = null): Pair<String, String> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        val tenant = tenantPersistencePort.findById(tenantId)
            ?: throw ResourceNotFoundException("Tenant", tenantId.toString())

        val report = reportPersistencePort.findById(reportId)
            ?: throw ResourceNotFoundException("Relatório", reportId.toString())

        val resolvedIds = formResponseIds
            ?: command.formResponseIds
            ?: listOfNotNull(command.formResponseId)
            ?: listOfNotNull(report.formResponseId)

        val formResponses = if (resolvedIds.isNotEmpty()) {
            formPersistencePort.findResponsesByIds(resolvedIds)
        } else {
            emptyList()
        }

        if (formResponses.isEmpty()) {
            throw BusinessException("Nenhum questionário respondido vinculado a este relatório. Vincule um questionário antes de gerar com IA.")
        }

        log.info("AI generation context: formResponseIds={}, totalAnswers={}", resolvedIds, formResponses.sumOf { it.answers.size })

        val customer = report.customerId?.let { customerPersistencePort.findById(it) }
        val professional = professionalPersistencePort.find()
        val template = findLinkedTemplate(formResponses.first().formId)

        val systemPrompt = systemPromptBuilder.build(tenant.vertical)
        val contextPrompt = sectionPromptBuilder.buildContext(
            customer, formResponses, template, professional, command.quantitativeData
        )

        val userPrompt = if (!command.previousSections.isNullOrEmpty()) {
            contextPrompt + "\n\n" + sectionPromptBuilder.buildUserPromptWithContext(
                command.sectionType, command.previousSections, tenant.vertical
            )
        } else {
            contextPrompt + "\n\n" + sectionPromptBuilder.buildUserPrompt(command.sectionType, tenant.vertical)
        }

        return Pair(systemPrompt, userPrompt)
    }

    private fun findLinkedTemplate(formId: UUID): com.dox.domain.model.ReportTemplate? {
        val form = formPersistencePort.findFormById(formId) ?: return null
        val templateId = form.linkedTemplateId ?: return null
        return templatePersistencePort.findAllReportTemplates().find { it.id == templateId }
    }

    fun summarizeForContext(sectionType: String, text: String): String {
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

    private fun executeAiGeneration(systemPrompt: String, userPrompt: String, model: String): AiGenerationResult {
        return executeWithRetry(systemPrompt, userPrompt, model)
    }

    private fun recordUsage(
        reportId: UUID,
        userId: UUID,
        sectionType: String,
        result: AiGenerationResult,
        costBrl: BigDecimal
    ) {
        val usage = AiUsage(
            reportId = reportId,
            generationId = result.generationId,
            professionalId = userId,
            sectionType = sectionType,
            model = result.model,
            inputTokens = result.inputTokens,
            outputTokens = result.outputTokens,
            cacheReadTokens = result.cacheReadTokens,
            cacheWriteTokens = result.cacheWriteTokens,
            estimatedCostBrl = costBrl,
            status = AiGenerationStatus.SUCCESS,
            durationMs = result.durationMs,
            isRegeneration = false,
            regenerationCount = 0
        )
        aiUsagePort.save(usage)
    }

    private fun recordFailure(
        reportId: UUID,
        userId: UUID,
        sectionType: String,
        model: String,
        errorMessage: String?
    ) {
        val usage = AiUsage(
            reportId = reportId,
            professionalId = userId,
            sectionType = sectionType,
            model = model,
            status = AiGenerationStatus.ERROR,
            errorMessage = errorMessage
        )
        aiUsagePort.save(usage)
    }

    override fun generateFullReport(
        command: GenerateFullReportCommand,
        onSectionProgress: (SectionProgressEvent) -> Unit
    ) {
        val userId = ContextHolder.getUserIdOrThrow()
        val tenantId = ContextHolder.getTenantIdOrThrow()

        val (quota, semaphore) = validateAndAcquireQuota(tenantId, command.reportId)

        try {
            val report = reportPersistencePort.findById(command.reportId)
                ?: throw ResourceNotFoundException("Relatório", command.reportId.toString())

            val resolvedFormResponseIds = command.formResponseIds?.map { it }
                ?: listOfNotNull(command.formResponseId)
                    .ifEmpty { listOfNotNull(report.formResponseId) }

            val fillableBlocks = report.blocks
                .filter { block ->
                    val type = block["type"]?.toString()
                    when (type) {
                        "section" -> true
                        "info-box" -> true
                        else -> false
                    }
                }
                .sortedBy { (it["order"] as? Number)?.toInt() ?: 0 }

            if (fillableBlocks.isEmpty()) {
                throw BusinessException("Nenhum bloco de seção ou info-box encontrado no relatório")
            }

            val blocksToGenerate = if (command.selectedSections != null) {
                fillableBlocks.filter { block ->
                    extractSectionType(block) in command.selectedSections
                }
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

            val sectionTitles = blocksToGenerate.map { extractSectionType(it) }
            val plan = buildGenerationPlan(tenant.vertical, sectionTitles, command, quota.model)
            val planMap = plan.sections.associateBy { it.title }

            log.info("Generation plan: {} sections — full={}, partial={}, skip={}",
                sectionTitles.size,
                plan.sections.count { it.status == "full" },
                plan.sections.count { it.status == "partial" },
                plan.sections.count { it.status == "skip" }
            )

            for ((index, block) in blocksToGenerate.withIndex()) {
                val sectionType = extractSectionType(block)
                val sectionPlan = planMap[sectionType] ?: SectionPlan(title = sectionType, status = "full")

                if (sectionPlan.status == "skip") {
                    skippedCount++

                    val warningMessage = sectionPlan.warning ?: "Dados insuficientes para gerar esta seção"
                    val blockType = block["type"]?.toString()
                    if (blockType == "info-box") {
                        updateBlockContent(command.reportId, block, warningMessage, skipped = true)
                    } else {
                        insertContentBlockAfter(command.reportId, block, warningMessage, skipped = true)
                    }

                    onSectionProgress(
                        SectionProgressEvent(
                            sectionType = sectionType,
                            index = index + 1,
                            total = total,
                            status = "skipped",
                            message = warningMessage
                        )
                    )
                    continue
                }

                try {
                    val sectionCommand = GenerateSectionCommand(
                        reportId = command.reportId,
                        sectionType = sectionType,
                        formResponseId = command.formResponseId,
                        formResponseIds = resolvedFormResponseIds.ifEmpty { null },
                        previousSections = previousSections.toList(),
                        quantitativeData = command.quantitativeData
                    )

                    val (systemPrompt, userPrompt) = buildGenerationContext(command.reportId, sectionCommand, resolvedFormResponseIds.ifEmpty { null })

                    val finalUserPrompt = if (sectionPlan.status == "partial" && sectionPlan.warning != null) {
                        "$userPrompt\n\nATENÇÃO: ${sectionPlan.warning}. Gere o texto com base nos dados disponíveis e indique claramente onde dados complementares seriam necessários."
                    } else {
                        userPrompt
                    }

                    val result = executeAiGeneration(systemPrompt, finalUserPrompt, quota.model)
                    val sanitizedText = sanitizeOutput(result.text)

                    if (sanitizedText.startsWith("[DADOS_INSUFICIENTES]")) {
                        skippedCount++
                        val warningMessage = sanitizedText.removePrefix("[DADOS_INSUFICIENTES]:").trim()

                        val blockType = block["type"]?.toString()
                        if (blockType == "info-box") {
                            updateBlockContent(command.reportId, block, warningMessage, skipped = true)
                        } else {
                            insertContentBlockAfter(command.reportId, block, warningMessage, skipped = true)
                        }

                        onSectionProgress(
                            SectionProgressEvent(
                                sectionType = sectionType,
                                index = index + 1,
                                total = total,
                                status = "skipped",
                                message = warningMessage
                            )
                        )
                        continue
                    }

                    val costBrl = calculateCost(result)

                    recordUsage(
                        reportId = command.reportId,
                        userId = userId,
                        sectionType = sectionType,
                        result = result,
                        costBrl = costBrl
                    )

                    val blockType = block["type"]?.toString()
                    if (blockType == "info-box") {
                        updateBlockContent(command.reportId, block, sanitizedText)
                    } else {
                        insertContentBlockAfter(command.reportId, block, sanitizedText)
                    }

                    previousSections.add(
                        PreviousSectionContext(sectionType, summarizeForContext(sectionType, sanitizedText))
                    )

                    totalTokens += result.inputTokens + result.outputTokens
                    totalCostBrl = totalCostBrl.add(costBrl)
                    completedCount++

                    onSectionProgress(
                        SectionProgressEvent(
                            sectionType = sectionType,
                            index = index + 1,
                            total = total,
                            status = "completed",
                            text = sanitizedText,
                            generationId = result.generationId.toString(),
                            tokensUsed = result.inputTokens + result.outputTokens,
                            warning = sectionPlan.warning
                        )
                    )
                } catch (e: Exception) {
                    if (e is BusinessException && e.message?.contains("Plano de IA") == true) throw e

                    log.error("Failed to generate section '{}' for report {}: {}", sectionType, command.reportId, e.message)
                    failedCount++

                    recordFailure(
                        reportId = command.reportId,
                        userId = userId,
                        sectionType = sectionType,
                        model = quota.model,
                        errorMessage = e.message?.take(500)
                    )

                    onSectionProgress(
                        SectionProgressEvent(
                            sectionType = sectionType,
                            index = index + 1,
                            total = total,
                            status = "error",
                            message = e.message?.take(200) ?: "Erro desconhecido"
                        )
                    )
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

            onSectionProgress(
                SectionProgressEvent(
                    sectionType = "_complete",
                    index = total,
                    total = total,
                    status = "done",
                    message = GenerationCompleteEvent(
                        completedCount = completedCount,
                        failedCount = failedCount,
                        totalTokens = totalTokens,
                        totalCostBrl = totalCostBrl.setScale(4, RoundingMode.HALF_UP).toPlainString()
                    ).toString()
                )
            )
        } finally {
            semaphore.release()
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
            val formResponseId = command.formResponseId ?: report.formResponseId
            val formResponse = formResponseId?.let { formPersistencePort.findResponseById(it) }

            val dataSummary = buildDataSummary(formResponse, command)
            val titlesText = sectionTitles.mapIndexed { i, t -> "${i + 1}. $t" }.joinToString("\n")

            val planningPrompt = systemPromptBuilder.buildPlanningPrompt(vertical, titlesText, dataSummary)
                ?: return defaultPlan(sectionTitles)

            val systemPrompt = systemPromptBuilder.build(vertical)
            val result = aiGenerationPort.generateSection(systemPrompt, planningPrompt, model, maxTokens = 2000)

            return planningResponseParser.parse(result.text, sectionTitles)
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

    private fun extractSectionType(block: Map<String, Any?>): String {
        val data = block["data"] as? Map<*, *> ?: return "Seção"
        val title = data["title"]?.toString()
        val subtitle = data["subtitle"]?.toString()
        val label = data["label"]?.toString()
        return title?.takeIf { it.isNotBlank() }
            ?: subtitle?.takeIf { it.isNotBlank() }
            ?: label?.takeIf { it.isNotBlank() }
            ?: "Seção"
    }

    private fun updateBlockContent(reportId: UUID, block: Map<String, Any?>, generatedText: String, skipped: Boolean = false) {
        val report = reportPersistencePort.findById(reportId) ?: return
        val blockId = block["id"]?.toString() ?: return

        val slateContent = textToSlateNodes(generatedText)

        val updatedBlocks = report.blocks.map { existingBlock ->
            if (existingBlock["id"]?.toString() == blockId) {
                val existingData = (existingBlock["data"] as? Map<*, *>)?.toMutableMap() ?: mutableMapOf()
                existingData["content"] = slateContent
                existingData["generatedByAi"] = true

                val mutableBlock = existingBlock.toMutableMap()
                mutableBlock["data"] = existingData
                if (skipped) mutableBlock["skippedByAi"] = true
                mutableBlock
            } else {
                existingBlock
            }
        }

        reportPersistencePort.save(report.copy(blocks = updatedBlocks))
    }

    private fun insertContentBlockAfter(reportId: UUID, sectionBlock: Map<String, Any?>, generatedText: String, skipped: Boolean = false) {
        val report = reportPersistencePort.findById(reportId) ?: return
        val sectionBlockId = sectionBlock["id"]?.toString() ?: return

        val slateContent = textToSlateNodes(generatedText)
        val sectionIndex = report.blocks.indexOfFirst { it["id"]?.toString() == sectionBlockId }
        if (sectionIndex < 0) return

        val nextBlock = report.blocks.getOrNull(sectionIndex + 1)
        val nextData = nextBlock?.get("data") as? Map<*, *>
        val isExistingAiBlock = nextBlock != null
            && (nextBlock["generatedByAi"] == true || nextBlock["skippedByAi"] == true)
            && nextData?.get("title")?.toString().isNullOrBlank()

        if (isExistingAiBlock) {
            val updatedBlocks = report.blocks.mapIndexed { index, block ->
                if (index == sectionIndex + 1) {
                    val existingData = (block["data"] as? Map<*, *>)?.toMutableMap() ?: mutableMapOf()
                    existingData["content"] = slateContent

                    val mutableBlock = block.toMutableMap()
                    mutableBlock["data"] = existingData
                    mutableBlock["generatedByAi"] = true
                    if (skipped) mutableBlock["skippedByAi"] = true else mutableBlock.remove("skippedByAi")
                    mutableBlock
                } else {
                    block
                }
            }
            reportPersistencePort.save(report.copy(blocks = updatedBlocks))
        } else {
            val sectionBlock = report.blocks.find { it["id"]?.toString() == sectionBlockId }
            val blockFields = mutableMapOf<String, Any?>(
                "id" to UUID.randomUUID().toString(),
                "type" to "text",
                "parentId" to (sectionBlock?.get("id")?.toString()),
                "order" to 0,
                "collapsed" to false,
                "generatedByAi" to true,
                "data" to mapOf(
                    "content" to slateContent,
                    "labeledItems" to emptyList<Any>(),
                    "useLabeledItems" to false
                )
            )
            if (skipped) blockFields["skippedByAi"] = true

            val updatedBlocks = mutableListOf<Map<String, Any?>>()
            for (existingBlock in report.blocks) {
                updatedBlocks.add(existingBlock)
                if (existingBlock["id"]?.toString() == sectionBlockId) {
                    updatedBlocks.add(blockFields)
                }
            }

            var order = 0
            val reorderedBlocks = updatedBlocks.map { block ->
                val mutableBlock = block.toMutableMap()
                mutableBlock["order"] = order++
                mutableBlock
            }

            reportPersistencePort.save(report.copy(blocks = reorderedBlocks))
        }
    }

    private fun textToSlateNodes(text: String): List<Map<String, Any>> {
        val paragraphs = text.split("\n\n").filter { it.isNotBlank() }
        if (paragraphs.isEmpty()) {
            return listOf(mapOf(
                "id" to generateSlateId(),
                "type" to "p",
                "children" to listOf(mapOf("text" to ""))
            ))
        }
        return paragraphs.map { paragraph ->
            mapOf(
                "id" to generateSlateId(),
                "type" to "p",
                "children" to listOf(mapOf("text" to paragraph.trim()))
            )
        }
    }

    private fun generateSlateId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-"
        return (1..10).map { chars.random() }.joinToString("")
    }

    @Transactional
    override fun regenerateSection(command: RegenerateSectionCommand): AiGenerationResult {
        val regenCount = aiUsagePort.countByReportId(command.reportId)
        if (regenCount >= aiConfig.maxRegenerationsPerReport) {
            throw BusinessException(
                "Limite de regenerações atingido ($regenCount de ${aiConfig.maxRegenerationsPerReport})"
            )
        }

        val result = generateSection(
            GenerateSectionCommand(
                reportId = command.reportId,
                sectionType = command.sectionType
            )
        )

        return result
    }

    override fun getUsageSummary(command: GetAiUsageCommand): AiUsageSummary {
        val userId = ContextHolder.getUserIdOrThrow()
        val quota = aiQuotaPort.findQuota()

        val used = aiUsagePort.countByProfessionalAndMonth(userId, command.month, command.year)
        val limit = quota?.monthlyLimit ?: 0
        val overage = if (used > limit) used - limit else 0
        val overageCostCents = overage * (quota?.overagePriceCents ?: 0)

        val alertLevel = resolveAlertLevel(used, limit)

        return AiUsageSummary(
            used = used,
            limit = limit,
            overage = overage,
            overageCostCents = overageCostCents,
            quota = quota,
            alertLevel = alertLevel
        )
    }

    override fun getUsageHistory(command: GetAiUsageCommand): List<AiUsage> {
        val userId = ContextHolder.getUserIdOrThrow()
        return aiUsagePort.findByProfessionalAndMonth(userId, command.month, command.year)
    }

    override fun getUsageByReport(reportId: UUID): List<AiUsage> =
        aiUsagePort.findByReportId(reportId)

    override fun getQuota(): AiQuota? =
        aiQuotaPort.findQuota()

    override fun updateQuota(command: UpdateAiQuotaCommand): AiQuota {
        val existing = aiQuotaPort.findQuota() ?: AiQuota()
        val updated = existing.copy(
            aiTier = command.aiTier?.let { AiTier.valueOf(it) } ?: existing.aiTier,
            model = command.model ?: existing.model,
            monthlyLimit = command.monthlyLimit ?: existing.monthlyLimit,
            overagePriceCents = command.overagePriceCents ?: existing.overagePriceCents,
            enabled = command.enabled ?: existing.enabled
        )
        return aiQuotaPort.save(updated)
    }

    override fun getGenerationSources(reportId: UUID): List<AiGenerationSource> =
        aiGenerationSourcePort.findByReportId(reportId)

    override fun getAiStatus(): AiStatus {
        val quota = aiQuotaPort.findQuota()
        val available = aiConfig.enabled && quota?.enabled == true && quota.aiTier != AiTier.NONE
        return AiStatus(
            available = available,
            tierName = quota?.aiTier?.name,
            model = quota?.model
        )
    }

    private fun resolveAlertLevel(used: Int, limit: Int): AlertLevel? {
        if (limit <= 0) return null
        return when {
            used > limit -> AlertLevel.OVERAGE
            used >= limit -> AlertLevel.LIMIT_REACHED
            used >= (limit * 0.8).toInt() -> AlertLevel.WARNING_80
            else -> null
        }
    }

    private fun executeWithRetry(systemPrompt: String, userPrompt: String, model: String): AiGenerationResult {
        return try {
            aiGenerationPort.generateSection(systemPrompt, userPrompt, model)
        } catch (e: RuntimeException) {
            if (e is BusinessException || e is ResourceNotFoundException || e is IllegalArgumentException) throw e
            log.warn("First attempt failed, retrying: {}", e.message)
            aiGenerationPort.generateSection(systemPrompt, userPrompt, model)
        }
    }

    private fun sanitizeOutput(text: String): String {
        var cleaned = Jsoup.clean(text.trim(), Safelist.none())

        if (!cleaned.contains("\n")) {
            cleaned = cleaned.replace(Regex("([.!?])\\s{2,}([A-ZÀ-Ú])")) {
                "${it.groupValues[1]}\n\n${it.groupValues[2]}"
            }
        }

        return cleaned
    }

    private fun calculateCost(result: AiGenerationResult): BigDecimal {
        val inputCost = result.inputTokens.toBigDecimal()
            .multiply(BigDecimal.valueOf(aiConfig.cost.sonnetInputPerMillion))
            .divide(BigDecimal.valueOf(1_000_000), 6, RoundingMode.HALF_UP)

        val outputCost = result.outputTokens.toBigDecimal()
            .multiply(BigDecimal.valueOf(aiConfig.cost.sonnetOutputPerMillion))
            .divide(BigDecimal.valueOf(1_000_000), 6, RoundingMode.HALF_UP)

        val cacheReadCost = result.cacheReadTokens.toBigDecimal()
            .multiply(BigDecimal.valueOf(aiConfig.cost.sonnetCacheReadPerMillion))
            .divide(BigDecimal.valueOf(1_000_000), 6, RoundingMode.HALF_UP)

        val cacheWriteCost = result.cacheWriteTokens.toBigDecimal()
            .multiply(BigDecimal.valueOf(aiConfig.cost.sonnetCacheWritePerMillion))
            .divide(BigDecimal.valueOf(1_000_000), 6, RoundingMode.HALF_UP)

        val totalUsd = inputCost + outputCost + cacheReadCost + cacheWriteCost
        return totalUsd.multiply(BigDecimal.valueOf(aiConfig.cost.brlUsdRate))
            .setScale(4, RoundingMode.HALF_UP)
    }
}
