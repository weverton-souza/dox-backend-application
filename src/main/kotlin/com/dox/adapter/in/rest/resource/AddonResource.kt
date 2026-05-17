package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.billing.AddonResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Add-ons", description = "Add-ons opcionais para complementar bundles")
@RequestMapping("/addons")
interface AddonResource : BaseResource {
    @Operation(summary = "Lista add-ons ativos disponíveis para assinatura")
    @GetMapping
    fun list(): ResponseEntity<List<AddonResponse>>

    @Operation(summary = "Obter add-on ativo por id")
    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: String,
    ): ResponseEntity<AddonResponse>
}
