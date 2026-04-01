package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.AiUsageJpaEntity
import com.dox.adapter.out.persistence.repository.AiUsageJpaRepository
import com.dox.application.port.output.AiUsagePort
import com.dox.domain.enum.AiGenerationStatus
import com.dox.domain.model.AiUsage
import com.dox.domain.model.TokenSummary
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Component
class AiUsagePersistenceAdapter(
    private val aiUsageJpaRepository: AiUsageJpaRepository,
) : AiUsagePort {
    override fun save(usage: AiUsage): AiUsage {
        val entity =
            AiUsageJpaEntity().apply {
                id = usage.id
                reportId = usage.reportId
                generationId = usage.generationId
                professionalId = usage.professionalId
                sectionType = usage.sectionType
                model = usage.model
                inputTokens = usage.inputTokens
                outputTokens = usage.outputTokens
                cacheReadTokens = usage.cacheReadTokens
                cacheWriteTokens = usage.cacheWriteTokens
                estimatedCostBrl = usage.estimatedCostBrl
                status = usage.status
                errorMessage = usage.errorMessage
                durationMs = usage.durationMs
                isRegeneration = usage.isRegeneration
                regenerationCount = usage.regenerationCount
            }
        return aiUsageJpaRepository.save(entity).toDomain()
    }

    override fun countByProfessionalAndMonth(
        professionalId: UUID,
        month: Int,
        year: Int,
    ): Int {
        val (start, end) = monthRange(month, year)
        return aiUsageJpaRepository.countSuccessByProfessionalAndPeriod(professionalId, start, end)
    }

    override fun countByReportId(reportId: UUID): Int = aiUsageJpaRepository.countByReportIdAndStatusNot(reportId, AiGenerationStatus.ERROR)

    override fun findByProfessionalAndMonth(
        professionalId: UUID,
        month: Int,
        year: Int,
    ): List<AiUsage> {
        val (start, end) = monthRange(month, year)
        return aiUsageJpaRepository.findByProfessionalAndPeriod(professionalId, start, end)
            .map { it.toDomain() }
    }

    override fun findByReportId(reportId: UUID): List<AiUsage> = aiUsageJpaRepository.findByReportId(reportId).map { it.toDomain() }

    override fun sumTokensByProfessionalAndMonth(
        professionalId: UUID,
        month: Int,
        year: Int,
    ): TokenSummary {
        val (start, end) = monthRange(month, year)
        val row = aiUsageJpaRepository.sumTokensByProfessionalAndPeriod(professionalId, start, end)
        return TokenSummary(
            totalInputTokens = (row[0] as Number).toLong(),
            totalOutputTokens = (row[1] as Number).toLong(),
            totalCacheReadTokens = (row[2] as Number).toLong(),
            totalCacheWriteTokens = (row[3] as Number).toLong(),
            totalCostBrl = (row[4] as Number).let { BigDecimal(it.toString()) },
        )
    }

    private fun monthRange(
        month: Int,
        year: Int,
    ): Pair<LocalDateTime, LocalDateTime> {
        val start = LocalDateTime.of(year, month, 1, 0, 0)
        val end = start.plusMonths(1)
        return start to end
    }

    private fun AiUsageJpaEntity.toDomain() =
        AiUsage(
            id = id,
            reportId = reportId,
            generationId = generationId,
            professionalId = professionalId,
            sectionType = sectionType,
            model = model,
            inputTokens = inputTokens,
            outputTokens = outputTokens,
            cacheReadTokens = cacheReadTokens,
            cacheWriteTokens = cacheWriteTokens,
            estimatedCostBrl = estimatedCostBrl,
            status = status,
            errorMessage = errorMessage,
            durationMs = durationMs,
            isRegeneration = isRegeneration,
            regenerationCount = regenerationCount,
            createdAt = createdAt,
        )
}
