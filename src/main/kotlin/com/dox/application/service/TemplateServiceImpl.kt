package com.dox.application.service

import com.dox.application.port.input.TemplateUseCase
import com.dox.application.port.output.TemplatePersistencePort
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.ChartTemplate
import com.dox.domain.model.ReportTemplate
import com.dox.domain.model.ScoreTableTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class TemplateServiceImpl(
    private val templatePersistencePort: TemplatePersistencePort,
) : TemplateUseCase {
    override fun getAllReportTemplates(): List<ReportTemplate> = templatePersistencePort.findAllReportTemplates()

    @Transactional
    override fun saveReportTemplate(template: ReportTemplate): ReportTemplate {
        val existing = templatePersistencePort.findReportTemplateById(template.id)
        if (existing?.isMaster == true) {
            throw BusinessException("Templates mestre não podem ser editados")
        }
        return templatePersistencePort.saveReportTemplate(template)
    }

    @Transactional
    override fun deleteReportTemplate(id: UUID) {
        val template =
            templatePersistencePort.findReportTemplateById(id)
                ?: throw ResourceNotFoundException("Template", id.toString())
        if (template.isMaster) {
            throw BusinessException("Templates mestre não podem ser excluídos")
        }
        templatePersistencePort.deleteReportTemplate(id)
    }

    @Transactional
    override fun duplicateReportTemplate(id: UUID): ReportTemplate {
        val source =
            templatePersistencePort.findReportTemplateById(id)
                ?: throw ResourceNotFoundException("Template", id.toString())
        return templatePersistencePort.saveReportTemplate(
            source.copy(
                id = UUID.randomUUID(),
                name = "Cópia de ${source.name}",
                isDefault = false,
                isLocked = false,
                isMaster = false,
            ),
        )
    }

    override fun getAllScoreTableTemplates(): List<ScoreTableTemplate> = templatePersistencePort.findAllScoreTableTemplates()

    @Transactional
    override fun saveScoreTableTemplate(template: ScoreTableTemplate): ScoreTableTemplate = templatePersistencePort.saveScoreTableTemplate(template)

    @Transactional
    override fun deleteScoreTableTemplate(id: UUID) = templatePersistencePort.deleteScoreTableTemplate(id)

    override fun getAllChartTemplates(): List<ChartTemplate> = templatePersistencePort.findAllChartTemplates()

    @Transactional
    override fun saveChartTemplate(template: ChartTemplate): ChartTemplate = templatePersistencePort.saveChartTemplate(template)

    @Transactional
    override fun deleteChartTemplate(id: UUID) = templatePersistencePort.deleteChartTemplate(id)
}
