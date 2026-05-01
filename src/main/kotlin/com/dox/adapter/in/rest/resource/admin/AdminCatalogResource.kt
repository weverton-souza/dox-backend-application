package com.dox.adapter.`in`.rest.resource.admin

import com.dox.adapter.`in`.rest.dto.admin.AdminBundleListItem
import com.dox.adapter.`in`.rest.dto.admin.AdminModuleListItem
import com.dox.adapter.`in`.rest.dto.admin.AdminModulePriceResponse
import com.dox.adapter.`in`.rest.dto.admin.UpdateBundleRequest
import com.dox.adapter.`in`.rest.dto.admin.UpdateModulePriceRequest
import com.dox.adapter.`in`.rest.resource.BaseResource
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Admin · Catálogo", description = "Edição de catálogo (módulos, bundles, addons) no backoffice")
@RequestMapping("/admin/catalog")
interface AdminCatalogResource : BaseResource {
    @Operation(summary = "Lista todos os módulos com o preço atual (cruza enum + module_prices)")
    @GetMapping("/modules")
    fun listModules(): ResponseEntity<List<AdminModuleListItem>>

    @Operation(summary = "Atualiza o preço do módulo (cria nova versão e expira a anterior)")
    @PutMapping("/modules/{moduleId}/price")
    fun updateModulePrice(
        @PathVariable moduleId: String,
        @Valid @RequestBody request: UpdateModulePriceRequest,
    ): ResponseEntity<AdminModulePriceResponse>

    @Operation(summary = "Histórico de versões de preço de um módulo")
    @GetMapping("/modules/{moduleId}/history")
    fun listModulePriceHistory(
        @PathVariable moduleId: String,
        @RequestParam(defaultValue = "20") limit: Int,
    ): ResponseEntity<List<AdminModulePriceResponse>>

    @Operation(summary = "Lista todos os bundles (tiers comerciais), inclusive inativos")
    @GetMapping("/bundles")
    fun listBundles(): ResponseEntity<List<AdminBundleListItem>>

    @Operation(summary = "Atualiza campos editáveis do bundle (preços, seats, slots, highlighted, sortOrder, description)")
    @PutMapping("/bundles/{bundleId}")
    fun updateBundle(
        @PathVariable bundleId: String,
        @Valid @RequestBody request: UpdateBundleRequest,
    ): ResponseEntity<AdminBundleListItem>
}
