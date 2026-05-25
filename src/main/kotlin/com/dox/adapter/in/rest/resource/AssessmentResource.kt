package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.assessment.AssessmentRequest
import com.dox.adapter.`in`.rest.dto.assessment.AssessmentResponse
import com.dox.adapter.`in`.rest.dto.assessment.RelatedTemplateResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Tag(name = "Avaliações", description = "Sessões de avaliação aplicada com registros (tabela, gráfico ou simples)")
@RequestMapping
interface AssessmentResource : BaseResource {
    @Operation(summary = "Listar avaliações de um cliente")
    @GetMapping("/customers/{customerId}/assessments")
    fun list(
        @PathVariable customerId: UUID,
        @RequestParam parameters: Map<String, Any>,
    ): ResponseEntity<Page<AssessmentResponse>>

    @Operation(summary = "Criar nova avaliação")
    @PostMapping("/customers/{customerId}/assessments")
    fun create(
        @PathVariable customerId: UUID,
        @Valid @RequestBody request: AssessmentRequest,
    ): ResponseEntity<AssessmentResponse>

    @Operation(summary = "Buscar avaliação por ID")
    @GetMapping("/customers/{customerId}/assessments/{id}")
    fun findById(
        @PathVariable customerId: UUID,
        @PathVariable id: UUID,
    ): ResponseEntity<AssessmentResponse>

    @Operation(summary = "Atualizar avaliação")
    @PutMapping("/customers/{customerId}/assessments/{id}")
    fun update(
        @PathVariable customerId: UUID,
        @PathVariable id: UUID,
        @Valid @RequestBody request: AssessmentRequest,
    ): ResponseEntity<AssessmentResponse>

    @Operation(summary = "Excluir avaliação (soft delete)")
    @DeleteMapping("/customers/{customerId}/assessments/{id}")
    fun delete(
        @PathVariable customerId: UUID,
        @PathVariable id: UUID,
    ): ResponseEntity<Void>

    @Operation(summary = "Autocomplete de nomes de instrumento (busca em templates de tabela e gráfico)")
    @GetMapping("/assessments/instruments/autocomplete")
    fun autocomplete(
        @RequestParam q: String,
    ): ResponseEntity<List<String>>

    @Operation(summary = "Templates de tabela e gráfico relacionados aos instrumentos da avaliação")
    @GetMapping("/customers/{customerId}/assessments/{id}/related-templates")
    fun relatedTemplates(
        @PathVariable customerId: UUID,
        @PathVariable id: UUID,
    ): ResponseEntity<List<RelatedTemplateResponse>>
}
