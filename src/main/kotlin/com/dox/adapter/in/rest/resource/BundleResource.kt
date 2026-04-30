package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.billing.BundleResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Bundles", description = "Pacotes pré-configurados de módulos")
@RequestMapping("/bundles")
interface BundleResource : BaseResource {
    @Operation(summary = "Listar bundles ativos com módulos expandidos")
    @GetMapping
    fun list(): ResponseEntity<List<BundleResponse>>

    @Operation(summary = "Obter bundle por id")
    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: String,
    ): ResponseEntity<BundleResponse>
}
