package com.dox.application.service

import com.dox.application.port.input.AiStatus
import com.dox.application.port.input.AiUsageSummary
import com.dox.application.port.input.AlertLevel
import com.dox.application.port.input.GetAiUsageCommand
import com.dox.application.port.input.RegenerationInfo
import com.dox.application.port.input.UpdateAiQuotaCommand
import com.dox.application.port.output.AiConfigPort
import com.dox.application.port.output.AiGenerationSourcePersistencePort
import com.dox.application.port.output.AiQuotaPort
import com.dox.application.port.output.AiUsagePort
import com.dox.domain.enum.AiTier
import com.dox.domain.model.AiGenerationSource
import com.dox.domain.model.AiQuota
import com.dox.domain.model.AiUsage
import com.dox.shared.ContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class AiUsageService(
    private val aiUsagePort: AiUsagePort,
    private val aiQuotaPort: AiQuotaPort,
    private val aiGenerationSourcePort: AiGenerationSourcePersistencePort,
    private val aiConfigPort: AiConfigPort,
) {
    fun getUsageSummary(command: GetAiUsageCommand): AiUsageSummary {
        val userId = ContextHolder.getUserIdOrThrow()
        val quota = aiQuotaPort.findQuota()

        val used = aiUsagePort.countByProfessionalAndMonth(userId, command.month, command.year)
        val limit = quota?.monthlyLimit ?: 0
        val overage = if (used > limit) used - limit else 0
        val overageCostCents = overage * (quota?.overagePriceCents ?: 0)

        return AiUsageSummary(
            used = used,
            limit = limit,
            overage = overage,
            overageCostCents = overageCostCents,
            quota = quota,
            alertLevel = resolveAlertLevel(used, limit),
        )
    }

    fun getUsageHistory(command: GetAiUsageCommand): List<AiUsage> {
        val userId = ContextHolder.getUserIdOrThrow()
        return aiUsagePort.findByProfessionalAndMonth(userId, command.month, command.year)
    }

    fun getUsageByReport(reportId: UUID): List<AiUsage> = aiUsagePort.findByReportId(reportId)

    fun getQuota(): AiQuota? = aiQuotaPort.findQuota()

    @Transactional
    fun updateQuota(command: UpdateAiQuotaCommand): AiQuota {
        val existing = aiQuotaPort.findQuota() ?: AiQuota()
        val updated =
            existing.copy(
                aiTier = command.aiTier?.let { AiTier.valueOf(it) } ?: existing.aiTier,
                model = command.model ?: existing.model,
                monthlyLimit = command.monthlyLimit ?: existing.monthlyLimit,
                overagePriceCents = command.overagePriceCents ?: existing.overagePriceCents,
                enabled = command.enabled ?: existing.enabled,
            )
        return aiQuotaPort.save(updated)
    }

    fun getAiStatus(): AiStatus {
        val quota = aiQuotaPort.findQuota()
        val available = aiConfigPort.isEnabled() && quota?.enabled == true && quota.aiTier != AiTier.NONE
        return AiStatus(
            available = available,
            tierName = quota?.aiTier?.name,
            model = quota?.model,
        )
    }

    fun getGenerationSources(reportId: UUID): List<AiGenerationSource> = aiGenerationSourcePort.findByReportId(reportId)

    fun getRegenerationInfo(reportId: UUID): RegenerationInfo =
        RegenerationInfo(
            used = aiUsagePort.countByReportId(reportId),
            limit = aiConfigPort.regenerationLimit(),
        )

    private fun resolveAlertLevel(
        used: Int,
        limit: Int,
    ): AlertLevel? {
        if (limit <= 0) return null
        return when {
            used > limit -> AlertLevel.OVERAGE
            used >= limit -> AlertLevel.LIMIT_REACHED
            used >= (limit * 0.8).toInt() -> AlertLevel.WARNING_80
            else -> null
        }
    }
}
