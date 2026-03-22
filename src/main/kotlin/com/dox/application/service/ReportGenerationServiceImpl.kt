package com.dox.application.service

import com.dox.adapter.out.ai.config.AiConfig
import com.dox.adapter.out.ai.prompt.SectionPromptBuilder
import com.dox.adapter.out.ai.prompt.SystemPromptBuilder
import com.dox.application.port.input.AiStatus
import com.dox.application.port.input.AiUsageSummary
import com.dox.application.port.input.AlertLevel
import com.dox.application.port.input.GenerateSectionCommand
import com.dox.application.port.input.GetAiUsageCommand
import com.dox.application.port.input.RegenerateSectionCommand
import com.dox.application.port.input.ReportGenerationUseCase
import com.dox.application.port.input.UpdateAiQuotaCommand
import com.dox.application.port.output.AiGenerationPort
import com.dox.application.port.output.AiQuotaPort
import com.dox.application.port.output.AiUsagePort
import com.dox.application.port.output.CustomerPersistencePort
import com.dox.application.port.output.FormPersistencePort
import com.dox.application.port.output.ReportPersistencePort
import com.dox.application.port.output.TenantPersistencePort
import com.dox.domain.enum.AiGenerationStatus
import com.dox.domain.enum.AiTier
import com.dox.domain.model.AiQuota
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.AiGenerationResult
import com.dox.domain.model.AiUsage
import com.dox.shared.ContextHolder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore

@Service
class ReportGenerationServiceImpl(
    private val aiGenerationPort: AiGenerationPort,
    private val aiUsagePort: AiUsagePort,
    private val aiQuotaPort: AiQuotaPort,
    private val reportPersistencePort: ReportPersistencePort,
    private val customerPersistencePort: CustomerPersistencePort,
    private val formPersistencePort: FormPersistencePort,
    private val tenantPersistencePort: TenantPersistencePort,
    private val systemPromptBuilder: SystemPromptBuilder,
    private val sectionPromptBuilder: SectionPromptBuilder,
    private val aiConfig: AiConfig
) : ReportGenerationUseCase {

    private val log = LoggerFactory.getLogger(javaClass)
    private val tenantSemaphores = ConcurrentHashMap<String, Semaphore>()

    @Transactional
    override fun generateSection(command: GenerateSectionCommand): AiGenerationResult {
        val userId = ContextHolder.getUserIdOrThrow()
        val tenantId = ContextHolder.getTenantIdOrThrow()

        val quota = aiQuotaPort.findQuota()
            ?: throw BusinessException("IA não habilitada para este workspace")

        if (!quota.enabled || quota.aiTier == AiTier.NONE) {
            throw BusinessException("Plano de IA não ativo")
        }

        val report = reportPersistencePort.findById(command.reportId)
            ?: throw ResourceNotFoundException("Relatório", command.reportId.toString())

        val semaphore = tenantSemaphores.computeIfAbsent(tenantId.toString()) {
            Semaphore(aiConfig.maxConcurrentPerTenant)
        }

        if (!semaphore.tryAcquire()) {
            throw BusinessException("Limite de gerações simultâneas atingido. Tente novamente em instantes.")
        }

        try {
            val tenant = tenantPersistencePort.findById(tenantId)
                ?: throw ResourceNotFoundException("Tenant", tenantId.toString())

            val formResponseId = command.formResponseId ?: report.formResponseId

            val formResponse = formResponseId?.let { formPersistencePort.findResponseById(it) }

            if (formResponse == null) {
                throw BusinessException("Nenhum questionário respondido vinculado a este relatório. Vincule um questionário antes de gerar com IA.")
            }

            log.info("AI generation context: formResponseId={}, answersCount={}", formResponseId, formResponse.answers.size)

            val customer = report.customerId?.let { customerPersistencePort.findById(it) }

            val systemPrompt = systemPromptBuilder.build(tenant.vertical)
            val contextPrompt = sectionPromptBuilder.buildContext(customer, formResponse, null, null)
            val userPrompt = contextPrompt + "\n\n" + sectionPromptBuilder.buildUserPrompt(command.sectionType)

            val result = executeWithRetry(systemPrompt, userPrompt, quota.model)

            val sanitizedText = sanitizeOutput(result.text)
            val costBrl = calculateCost(result)

            val usage = AiUsage(
                reportId = command.reportId,
                generationId = result.generationId,
                professionalId = userId,
                sectionType = command.sectionType,
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

            return result.copy(text = sanitizedText)
        } catch (e: Exception) {
            if (e is BusinessException || e is ResourceNotFoundException) throw e

            log.error("AI generation failed for report {}: {}", command.reportId, e.message)

            val usage = AiUsage(
                reportId = command.reportId,
                professionalId = userId,
                sectionType = command.sectionType,
                model = quota.model,
                status = AiGenerationStatus.ERROR,
                errorMessage = e.message?.take(500)
            )
            aiUsagePort.save(usage)

            throw BusinessException("Erro ao gerar seção com IA. Tente novamente.")
        } finally {
            semaphore.release()
        }
    }

    @Transactional
    override fun regenerateSection(command: RegenerateSectionCommand): AiGenerationResult {
        val userId = ContextHolder.getUserIdOrThrow()

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
        var cleaned = text.trim()
        cleaned = cleaned.replace(Regex("<[^>]+>"), "")

        if (!cleaned.contains("\n")) {
            cleaned = cleaned.replace(Regex("([.!?])\\s*([A-ZÀ-Ú])")) {
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
