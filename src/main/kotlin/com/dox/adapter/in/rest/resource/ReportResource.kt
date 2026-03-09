package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.report.ReportRequest
import com.dox.adapter.`in`.rest.dto.report.ReportResponse
import com.dox.adapter.`in`.rest.dto.report.ReportVersionRequest
import com.dox.adapter.`in`.rest.dto.report.ReportVersionResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Tag(name = "Relatórios", description = "CRUD de relatórios e versionamento")
@RequestMapping("/reports")
interface ReportResource : BaseResource {

    @Operation(summary = "Listar relatórios com paginação")
    @GetMapping
    fun findAll(pageable: Pageable): ResponseEntity<Page<ReportResponse>>

    @Operation(summary = "Criar relatório")
    @PostMapping
    fun create(@RequestBody request: ReportRequest): ResponseEntity<ReportResponse>

    @Operation(summary = "Buscar relatório por ID")
    @GetMapping("/{id}")
    fun findById(@PathVariable id: UUID): ResponseEntity<ReportResponse>

    @Operation(summary = "Atualizar relatório")
    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody request: ReportRequest): ResponseEntity<ReportResponse>

    @Operation(summary = "Excluir relatório (soft delete)")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void>

    @Operation(summary = "Relatórios de um cliente")
    @GetMapping("/customer/{customerId}")
    fun findByCustomerId(@PathVariable customerId: UUID): ResponseEntity<List<ReportResponse>>

    @Operation(summary = "Listar versões de um relatório")
    @GetMapping("/{id}/versions")
    fun getVersions(@PathVariable id: UUID): ResponseEntity<List<ReportVersionResponse>>

    @Operation(summary = "Criar versão (snapshot) de um relatório")
    @PostMapping("/{id}/versions")
    fun createVersion(@PathVariable id: UUID, @RequestBody request: ReportVersionRequest): ResponseEntity<ReportVersionResponse>
}
