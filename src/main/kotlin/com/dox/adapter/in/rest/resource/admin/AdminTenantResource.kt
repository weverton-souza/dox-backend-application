package com.dox.adapter.`in`.rest.resource.admin

import com.dox.adapter.`in`.rest.dto.admin.AdminPagedResponse
import com.dox.adapter.`in`.rest.dto.admin.AdminSubscriptionInfo
import com.dox.adapter.`in`.rest.dto.admin.AdminTenantDetailResponse
import com.dox.adapter.`in`.rest.dto.admin.AdminTenantListItem
import com.dox.adapter.`in`.rest.dto.admin.AdminTenantModuleInfo
import com.dox.adapter.`in`.rest.dto.admin.ExtendTrialRequest
import com.dox.adapter.`in`.rest.dto.admin.GrantModuleRequest
import com.dox.adapter.`in`.rest.resource.BaseResource
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Tag(name = "Admin · Tenants", description = "Listagem, drill-down e ações de tenants para o backoffice")
@RequestMapping("/admin/tenants")
interface AdminTenantResource : BaseResource {
    @Operation(summary = "Lista paginada de tenants com busca por nome")
    @GetMapping
    fun list(
        @RequestParam(required = false) search: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "15") size: Int,
    ): ResponseEntity<AdminPagedResponse<AdminTenantListItem>>

    @Operation(summary = "Drill-down de um tenant: subscription, módulos, bundle e pagamentos recentes")
    @GetMapping("/{id}")
    fun detail(
        @PathVariable id: UUID,
    ): ResponseEntity<AdminTenantDetailResponse>

    @Operation(summary = "Concede um módulo grátis ao tenant (source=GRANT)")
    @PostMapping("/{id}/grant-module")
    fun grantModule(
        @PathVariable id: UUID,
        @Valid @RequestBody request: GrantModuleRequest,
    ): ResponseEntity<AdminTenantModuleInfo>

    @Operation(summary = "Estende o trial do tenant em N dias")
    @PostMapping("/{id}/extend-trial")
    fun extendTrial(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ExtendTrialRequest,
    ): ResponseEntity<AdminSubscriptionInfo>
}
