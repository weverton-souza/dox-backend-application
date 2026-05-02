package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.formaggregation.AggregatedFormGroupResponse
import com.dox.adapter.`in`.rest.dto.formaggregation.ComparisonResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Tag(name = "Formulários do Cliente", description = "Status agregado e comparação multi-respondente")
@RequestMapping("/customers/{customerId}/forms")
interface CustomerFormsResource : BaseResource {
    @Operation(summary = "Lista links agrupados por (formId, versionId) com status por respondente")
    @GetMapping("/aggregated")
    fun aggregated(
        @PathVariable customerId: UUID,
    ): ResponseEntity<List<AggregatedFormGroupResponse>>

    @Operation(summary = "Comparação lado a lado de respondentes da mesma versão de um formulário")
    @GetMapping("/{formId}/comparison")
    fun comparison(
        @PathVariable customerId: UUID,
        @PathVariable formId: UUID,
        @RequestParam versionId: UUID,
    ): ResponseEntity<ComparisonResponse>
}
