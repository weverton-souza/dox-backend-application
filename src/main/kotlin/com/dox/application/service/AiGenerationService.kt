package com.dox.application.service

import com.dox.application.port.input.GenerateSectionCommand
import com.dox.application.port.input.RegenerateSectionCommand
import com.dox.application.port.input.ReviewTextCommand
import com.dox.application.port.output.AiConfigPort
import com.dox.application.port.output.AiGenerationPort
import com.dox.application.port.output.AiQuotaPort
import com.dox.application.port.output.AiReviewPromptPort
import com.dox.application.port.output.AiSectionPromptPort
import com.dox.application.port.output.AiSystemPromptPort
import com.dox.application.port.output.AiUsagePort
import com.dox.application.port.output.CustomerPersistencePort
import com.dox.application.port.output.FormPersistencePort
import com.dox.application.port.output.ProfessionalSettingsPersistencePort
import com.dox.application.port.output.ReportPersistencePort
import com.dox.application.port.output.TemplatePersistencePort
import com.dox.application.port.output.TenantPersistencePort
import com.dox.domain.enum.AiGenerationStatus
import com.dox.domain.enum.AiTier
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.AiGenerationResult
import com.dox.domain.model.AiQuota
import com.dox.domain.model.AiUsage
import com.dox.shared.ContextHolder
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore

@Service
class AiGenerationService(
    private val aiGenerationPort: AiGenerationPort,
    private val aiUsagePort: AiUsagePort,
    private val aiQuotaPort: AiQuotaPort,
    private val reportPersistencePort: ReportPersistencePort,
    private val customerPersistencePort: CustomerPersistencePort,
    private val formPersistencePort: FormPersistencePort,
    private val tenantPersistencePort: TenantPersistencePort,
    private val professionalPersistencePort: ProfessionalSettingsPersistencePort,
    private val templatePersistencePort: TemplatePersistencePort,
    private val systemPromptPort: AiSystemPromptPort,
    private val sectionPromptPort: AiSectionPromptPort,
    private val reviewPromptPort: AiReviewPromptPort,
    private val aiConfigPort: AiConfigPort
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val tenantSemaphores = ConcurrentHashMap<String, Semaphore>()

    private companion object {
        private const val MAX_SEMAPHORE_CACHE_SIZE = 1000
    }

    fun generateSection(command: GenerateSectionCommand): AiGenerationResult {
        val userId = ContextHolder.getUserIdOrThrow()
        val tenantId = ContextHolder.getTenantIdOrThrow()

        val (quota, semaphore) = validateAndAcquireQuota(tenantId, command.reportId)

        try {
            val (systemPrompt, userPrompt) = buildGenerationContext(command.reportId, command)

            val result = executeWithRetry(systemPrompt, userPrompt, quota.model)
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

            throw BusinessException("Erro ao gerar seção com o Assistente. Tente novamente.")
        } finally {
            semaphore.release()
        }
    }

    fun regenerateSection(command: RegenerateSectionCommand): AiGenerationResult {
        val regenCount = aiUsagePort.countByReportId(command.reportId)
        if (regenCount >= aiConfigPort.regenerationLimit()) {
            throw BusinessException(
                "Limite de regenerações atingido ($regenCount de ${aiConfigPort.regenerationLimit()})"
            )
        }

        return generateSection(
            GenerateSectionCommand(
                reportId = command.reportId,
                sectionType = command.sectionType
            )
        )
    }

    fun reviewText(command: ReviewTextCommand): AiGenerationResult {
        val userId = ContextHolder.getUserIdOrThrow()
        val tenantId = ContextHolder.getTenantIdOrThrow()

        val (quota, semaphore) = validateAndAcquireQuota(tenantId, command.reportId)

        try {
            val tenant = tenantPersistencePort.findById(tenantId)
                ?: throw ResourceNotFoundException("Tenant", tenantId.toString())

            val formResponses = if (!command.formResponseIds.isNullOrEmpty()) {
                formPersistencePort.findResponsesByIds(command.formResponseIds)
            } else {
                emptyList()
            }

            val systemPrompt = reviewPromptPort.buildSystemPrompt(tenant.vertical)
            val userPrompt = reviewPromptPort.buildUserPrompt(
                text = command.text,
                action = command.action,
                sectionType = command.sectionType,
                instruction = command.instruction,
                formResponses = formResponses.ifEmpty { null }
            )

            val result = executeWithRetry(systemPrompt, userPrompt, quota.model)
            val sanitizedText = sanitizeOutput(result.text)
            val costBrl = calculateCost(result)

            recordUsage(
                reportId = command.reportId,
                userId = userId,
                sectionType = "review:${command.action}",
                result = result,
                costBrl = costBrl
            )

            return result.copy(text = sanitizedText)
        } catch (e: Exception) {
            if (e is BusinessException || e is ResourceNotFoundException) throw e

            log.error("AI review failed for report {}: {}", command.reportId, e.message)

            recordFailure(
                reportId = command.reportId,
                userId = userId,
                sectionType = "review:${command.action}",
                model = quota.model,
                errorMessage = e.message?.take(500)
            )

            throw BusinessException("Erro ao revisar texto com Assistente. Tente novamente.")
        } finally {
            semaphore.release()
        }
    }

    fun validateAndAcquireQuota(tenantId: UUID, reportId: UUID): Pair<AiQuota, Semaphore> {
        val quota = aiQuotaPort.findQuota()
            ?: throw BusinessException("Assistente não habilitado para este workspace")

        if (!quota.enabled || quota.aiTier == AiTier.NONE) {
            throw BusinessException("Plano do Assistente não ativo")
        }

        reportPersistencePort.findById(reportId)
            ?: throw ResourceNotFoundException("Relatório", reportId.toString())

        val semaphore = tenantSemaphores.computeIfAbsent(tenantId.toString()) {
            Semaphore(aiConfigPort.concurrencyLimit())
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

    fun buildGenerationContext(reportId: UUID, command: GenerateSectionCommand, formResponseIds: List<UUID>? = null): Pair<String, String> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        val tenant = tenantPersistencePort.findById(tenantId)
            ?: throw ResourceNotFoundException("Tenant", tenantId.toString())

        val report = reportPersistencePort.findById(reportId)
            ?: throw ResourceNotFoundException("Relatório", reportId.toString())

        val resolvedIds = formResponseIds
            ?: command.formResponseIds
            ?: emptyList()

        val formResponses = if (resolvedIds.isNotEmpty()) {
            formPersistencePort.findResponsesByIds(resolvedIds)
        } else {
            emptyList()
        }

        val hasQuantitativeData = !command.quantitativeContext.isNullOrBlank() ||
            (command.quantitativeData?.tables?.any { it.dataStatus != "empty" } == true) ||
            (command.quantitativeData?.charts?.any { it.dataStatus != "empty" } == true)

        if (formResponses.isEmpty() && !hasQuantitativeData) {
            throw BusinessException("Nenhum questionário respondido ou dado quantitativo vinculado a este relatório.")
        }

        log.info(
            "AI generation context: section={}, formResponseIds={}, totalAnswers={}, hasQuantitative={}",
            command.sectionType,
            resolvedIds.size,
            formResponses.sumOf { it.answers.size },
            hasQuantitativeData
        )

        val customer = if (command.includeCustomerData) report.customerId?.let { customerPersistencePort.findById(it) } else null
        val professional = professionalPersistencePort.find()
        val template = if (formResponses.isNotEmpty()) findLinkedTemplate(formResponses.first().formId) else null

        val systemPrompt = systemPromptPort.build(tenant.vertical)
        val contextPrompt = sectionPromptPort.buildContext(
            customer,
            formResponses,
            template,
            professional,
            command.quantitativeData,
            command.quantitativeContext
        )

        val userPrompt = if (!command.previousSections.isNullOrEmpty()) {
            contextPrompt + "\n\n" + sectionPromptPort.buildUserPromptWithContext(
                command.sectionType, command.previousSections, tenant.vertical, command.instruction
            )
        } else {
            contextPrompt + "\n\n" + sectionPromptPort.buildUserPrompt(command.sectionType, tenant.vertical, command.instruction)
        }

        return Pair(systemPrompt, userPrompt)
    }

    fun executeWithRetry(systemPrompt: String, userPrompt: String, model: String): AiGenerationResult {
        return try {
            aiGenerationPort.generateSection(systemPrompt, userPrompt, model)
        } catch (e: RuntimeException) {
            if (e is BusinessException || e is ResourceNotFoundException || e is IllegalArgumentException) throw e
            log.warn("First attempt failed, retrying: {}", e.message)
            aiGenerationPort.generateSection(systemPrompt, userPrompt, model)
        }
    }

    fun sanitizeOutput(text: String): String {
        var cleaned = Jsoup.clean(text.trim(), Safelist.none())

        if (!cleaned.contains("\n")) {
            cleaned = cleaned.replace(Regex("([.!?])\\s{2,}([A-ZÀ-Ú])")) {
                "${it.groupValues[1]}\n\n${it.groupValues[2]}"
            }
        }

        return cleaned
    }

    fun calculateCost(result: AiGenerationResult): BigDecimal {
        val cost = aiConfigPort.costConfig()

        val inputCost = result.inputTokens.toBigDecimal()
            .multiply(BigDecimal.valueOf(cost.sonnetInputPerMillion))
            .divide(BigDecimal.valueOf(1_000_000), 6, RoundingMode.HALF_UP)

        val outputCost = result.outputTokens.toBigDecimal()
            .multiply(BigDecimal.valueOf(cost.sonnetOutputPerMillion))
            .divide(BigDecimal.valueOf(1_000_000), 6, RoundingMode.HALF_UP)

        val cacheReadCost = result.cacheReadTokens.toBigDecimal()
            .multiply(BigDecimal.valueOf(cost.sonnetCacheReadPerMillion))
            .divide(BigDecimal.valueOf(1_000_000), 6, RoundingMode.HALF_UP)

        val cacheWriteCost = result.cacheWriteTokens.toBigDecimal()
            .multiply(BigDecimal.valueOf(cost.sonnetCacheWritePerMillion))
            .divide(BigDecimal.valueOf(1_000_000), 6, RoundingMode.HALF_UP)

        val totalUsd = inputCost + outputCost + cacheReadCost + cacheWriteCost
        return totalUsd.multiply(BigDecimal.valueOf(cost.brlUsdRate))
            .setScale(4, RoundingMode.HALF_UP)
    }

    @Transactional
    fun recordUsage(
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

    @Transactional
    fun recordFailure(
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

    private fun findLinkedTemplate(formId: UUID): com.dox.domain.model.ReportTemplate? {
        val form = formPersistencePort.findFormById(formId) ?: return null
        val templateId = form.linkedTemplateId ?: return null
        return templatePersistencePort.findReportTemplateById(templateId)
    }
}
