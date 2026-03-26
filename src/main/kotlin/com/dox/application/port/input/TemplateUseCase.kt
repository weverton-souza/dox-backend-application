package com.dox.application.port.input

import com.dox.domain.model.ChartTemplate
import com.dox.domain.model.ReportTemplate
import com.dox.domain.model.ScoreTableTemplate
import java.util.UUID

interface TemplateUseCase {
    fun getAllReportTemplates(): List<ReportTemplate>
    fun saveReportTemplate(template: ReportTemplate): ReportTemplate
    fun deleteReportTemplate(id: UUID)
    fun duplicateReportTemplate(id: UUID): ReportTemplate

    fun getAllScoreTableTemplates(): List<ScoreTableTemplate>
    fun saveScoreTableTemplate(template: ScoreTableTemplate): ScoreTableTemplate
    fun deleteScoreTableTemplate(id: UUID)

    fun getAllChartTemplates(): List<ChartTemplate>
    fun saveChartTemplate(template: ChartTemplate): ChartTemplate
    fun deleteChartTemplate(id: UUID)
}
