package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.template.ChartTemplateRequest
import com.dox.adapter.`in`.rest.dto.template.ChartTemplateResponse
import com.dox.adapter.`in`.rest.dto.template.ReportTemplateRequest
import com.dox.adapter.`in`.rest.dto.template.ReportTemplateResponse
import com.dox.adapter.`in`.rest.dto.template.ScoreTableTemplateRequest
import com.dox.adapter.`in`.rest.dto.template.ScoreTableTemplateResponse
import com.dox.adapter.`in`.rest.resource.TemplateResource
import com.dox.application.port.input.TemplateUseCase
import com.dox.domain.model.ChartTemplate
import com.dox.domain.model.ReportTemplate
import com.dox.domain.model.ScoreTableTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class TemplateResourceImpl(
    private val templateUseCase: TemplateUseCase,
) : TemplateResource {
    override fun getReportTemplates(): ResponseEntity<List<ReportTemplateResponse>> = responseEntity(templateUseCase.getAllReportTemplates().map { it.toResponse() })

    override fun saveReportTemplate(request: ReportTemplateRequest): ResponseEntity<ReportTemplateResponse> =
        responseEntity(
            templateUseCase.saveReportTemplate(
                ReportTemplate(name = request.name, description = request.description, blocks = request.blocks, isLocked = request.isLocked),
            ).toResponse(),
            HttpStatus.CREATED,
        )

    override fun deleteReportTemplate(id: UUID): ResponseEntity<Void> {
        templateUseCase.deleteReportTemplate(id)
        return noContent()
    }

    override fun duplicateReportTemplate(id: UUID): ResponseEntity<ReportTemplateResponse> = responseEntity(templateUseCase.duplicateReportTemplate(id).toResponse(), HttpStatus.CREATED)

    override fun getScoreTableTemplates(): ResponseEntity<List<ScoreTableTemplateResponse>> = responseEntity(templateUseCase.getAllScoreTableTemplates().map { it.toResponse() })

    override fun saveScoreTableTemplate(request: ScoreTableTemplateRequest): ResponseEntity<ScoreTableTemplateResponse> =
        responseEntity(
            templateUseCase.saveScoreTableTemplate(
                ScoreTableTemplate(
                    name = request.name,
                    description = request.description,
                    instrumentName = request.instrumentName,
                    category = request.category,
                    columns = request.columns,
                    rows = request.rows,
                ),
            ).toResponse(),
            HttpStatus.CREATED,
        )

    override fun deleteScoreTableTemplate(id: UUID): ResponseEntity<Void> {
        templateUseCase.deleteScoreTableTemplate(id)
        return noContent()
    }

    override fun getChartTemplates(): ResponseEntity<List<ChartTemplateResponse>> = responseEntity(templateUseCase.getAllChartTemplates().map { it.toResponse() })

    override fun saveChartTemplate(request: ChartTemplateRequest): ResponseEntity<ChartTemplateResponse> =
        responseEntity(
            templateUseCase.saveChartTemplate(
                ChartTemplate(
                    name = request.name,
                    description = request.description,
                    instrumentName = request.instrumentName,
                    category = request.category,
                    data = request.data,
                ),
            ).toResponse(),
            HttpStatus.CREATED,
        )

    override fun deleteChartTemplate(id: UUID): ResponseEntity<Void> {
        templateUseCase.deleteChartTemplate(id)
        return noContent()
    }

    private fun ReportTemplate.toResponse() =
        ReportTemplateResponse(
            id, name, description, blocks, isDefault, isLocked, isMaster, createdAt, updatedAt,
        )

    private fun ScoreTableTemplate.toResponse() =
        ScoreTableTemplateResponse(
            id, name, description, instrumentName, category, columns, rows, footnote, isDefault, createdAt, updatedAt,
        )

    private fun ChartTemplate.toResponse() =
        ChartTemplateResponse(
            id, name, description, instrumentName, category, data, isDefault, createdAt, updatedAt,
        )
}
