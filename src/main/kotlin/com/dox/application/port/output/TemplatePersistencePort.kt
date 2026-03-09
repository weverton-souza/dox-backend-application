package com.dox.application.port.output

import com.dox.domain.model.ChartTemplate
import com.dox.domain.model.ReportTemplate
import com.dox.domain.model.ScoreTableTemplate
import java.util.UUID

interface TemplatePersistencePort {
    fun findAllReportTemplates(): List<ReportTemplate>
    fun saveReportTemplate(template: ReportTemplate): ReportTemplate
    fun deleteReportTemplate(id: UUID)

    fun findAllScoreTableTemplates(): List<ScoreTableTemplate>
    fun saveScoreTableTemplate(template: ScoreTableTemplate): ScoreTableTemplate
    fun deleteScoreTableTemplate(id: UUID)

    fun findAllChartTemplates(): List<ChartTemplate>
    fun saveChartTemplate(template: ChartTemplate): ChartTemplate
    fun deleteChartTemplate(id: UUID)
}
