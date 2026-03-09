package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.template.ChartTemplateRequest
import com.dox.adapter.`in`.rest.dto.template.ChartTemplateResponse
import com.dox.adapter.`in`.rest.dto.template.ReportTemplateRequest
import com.dox.adapter.`in`.rest.dto.template.ReportTemplateResponse
import com.dox.adapter.`in`.rest.dto.template.ScoreTableTemplateRequest
import com.dox.adapter.`in`.rest.dto.template.ScoreTableTemplateResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Tag(name = "Templates", description = "Templates de relatório, tabelas de escores e gráficos")
@RequestMapping("/templates")
interface TemplateResource : BaseResource {

    @Operation(summary = "Listar templates de relatório")
    @GetMapping("/reports")
    fun getReportTemplates(): ResponseEntity<List<ReportTemplateResponse>>

    @Operation(summary = "Salvar template de relatório")
    @PostMapping("/reports")
    fun saveReportTemplate(@RequestBody request: ReportTemplateRequest): ResponseEntity<ReportTemplateResponse>

    @Operation(summary = "Excluir template de relatório")
    @DeleteMapping("/reports/{id}")
    fun deleteReportTemplate(@PathVariable id: UUID): ResponseEntity<Void>

    @Operation(summary = "Listar templates de tabela de escores")
    @GetMapping("/score-tables")
    fun getScoreTableTemplates(): ResponseEntity<List<ScoreTableTemplateResponse>>

    @Operation(summary = "Salvar template de tabela de escores")
    @PostMapping("/score-tables")
    fun saveScoreTableTemplate(@RequestBody request: ScoreTableTemplateRequest): ResponseEntity<ScoreTableTemplateResponse>

    @Operation(summary = "Excluir template de tabela de escores")
    @DeleteMapping("/score-tables/{id}")
    fun deleteScoreTableTemplate(@PathVariable id: UUID): ResponseEntity<Void>

    @Operation(summary = "Listar templates de gráfico")
    @GetMapping("/charts")
    fun getChartTemplates(): ResponseEntity<List<ChartTemplateResponse>>

    @Operation(summary = "Salvar template de gráfico")
    @PostMapping("/charts")
    fun saveChartTemplate(@RequestBody request: ChartTemplateRequest): ResponseEntity<ChartTemplateResponse>

    @Operation(summary = "Excluir template de gráfico")
    @DeleteMapping("/charts/{id}")
    fun deleteChartTemplate(@PathVariable id: UUID): ResponseEntity<Void>
}
