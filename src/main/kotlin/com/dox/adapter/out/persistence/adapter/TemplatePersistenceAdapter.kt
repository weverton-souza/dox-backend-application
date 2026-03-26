package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.ChartTemplateJpaEntity
import com.dox.adapter.out.persistence.entity.ReportTemplateJpaEntity
import com.dox.adapter.out.persistence.entity.ScoreTableTemplateJpaEntity
import com.dox.adapter.out.persistence.repository.ChartTemplateJpaRepository
import com.dox.adapter.out.persistence.repository.ReportTemplateJpaRepository
import com.dox.adapter.out.persistence.repository.ScoreTableTemplateJpaRepository
import com.dox.application.port.output.TemplatePersistencePort
import com.dox.domain.model.ChartTemplate
import com.dox.domain.model.ReportTemplate
import com.dox.domain.model.ScoreTableTemplate
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TemplatePersistenceAdapter(
    private val reportTemplateRepo: ReportTemplateJpaRepository,
    private val scoreTableTemplateRepo: ScoreTableTemplateJpaRepository,
    private val chartTemplateRepo: ChartTemplateJpaRepository
) : TemplatePersistencePort {

    override fun findAllReportTemplates(): List<ReportTemplate> =
        reportTemplateRepo.findAll().map { it.toDomain() }

    override fun findReportTemplateById(id: UUID): ReportTemplate? =
        reportTemplateRepo.findById(id).orElse(null)?.toDomain()

    override fun saveReportTemplate(template: ReportTemplate): ReportTemplate {
        val entity = reportTemplateRepo.findById(template.id).orElse(null)
            ?: ReportTemplateJpaEntity().apply { id = template.id }
        entity.name = template.name
        entity.description = template.description
        entity.blocks = template.blocks
        entity.isDefault = template.isDefault
        entity.isLocked = template.isLocked
        entity.isMaster = template.isMaster
        return reportTemplateRepo.save(entity).toDomain()
    }

    override fun deleteReportTemplate(id: UUID) = reportTemplateRepo.deleteById(id)

    override fun findAllScoreTableTemplates(): List<ScoreTableTemplate> =
        scoreTableTemplateRepo.findAll().map { it.toDomain() }

    override fun saveScoreTableTemplate(template: ScoreTableTemplate): ScoreTableTemplate {
        val entity = ScoreTableTemplateJpaEntity().apply {
            id = template.id; name = template.name; description = template.description
            instrumentName = template.instrumentName; category = template.category
            columns = template.columns; rows = template.rows; isDefault = template.isDefault
        }
        return scoreTableTemplateRepo.save(entity).toDomain()
    }

    override fun deleteScoreTableTemplate(id: UUID) = scoreTableTemplateRepo.deleteById(id)

    override fun findAllChartTemplates(): List<ChartTemplate> =
        chartTemplateRepo.findAll().map { it.toDomain() }

    override fun saveChartTemplate(template: ChartTemplate): ChartTemplate {
        val entity = ChartTemplateJpaEntity().apply {
            id = template.id; name = template.name; description = template.description
            instrumentName = template.instrumentName; category = template.category
            data = template.data; isDefault = template.isDefault
        }
        return chartTemplateRepo.save(entity).toDomain()
    }

    override fun deleteChartTemplate(id: UUID) = chartTemplateRepo.deleteById(id)

    private fun ReportTemplateJpaEntity.toDomain() = ReportTemplate(
        id, name, description, blocks, isDefault, isLocked, isMaster, createdAt, updatedAt
    )

    private fun ScoreTableTemplateJpaEntity.toDomain() = ScoreTableTemplate(
        id, name, description, instrumentName, category, columns, rows, isDefault, createdAt, updatedAt
    )

    private fun ChartTemplateJpaEntity.toDomain() = ChartTemplate(
        id, name, description, instrumentName, category, data, isDefault, createdAt, updatedAt
    )
}
