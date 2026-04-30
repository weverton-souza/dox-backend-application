package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.billing.ActiveModuleResponse
import com.dox.adapter.`in`.rest.dto.billing.ModuleAccessResponse
import com.dox.adapter.`in`.rest.dto.billing.ModuleCatalogResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Módulos", description = "Catálogo e entitlement de módulos do tenant")
@RequestMapping("/modules")
interface ModuleResource : BaseResource {
    @Operation(summary = "Catálogo público de módulos")
    @GetMapping("/catalog")
    fun catalog(): ResponseEntity<List<ModuleCatalogResponse>>

    @Operation(summary = "Módulos ativos do tenant atual")
    @GetMapping("/active")
    fun active(): ResponseEntity<List<ActiveModuleResponse>>

    @Operation(summary = "Nível de acesso por módulo do tenant atual")
    @GetMapping("/accessible")
    fun accessible(): ResponseEntity<List<ModuleAccessResponse>>
}
