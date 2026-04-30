package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.billing.ActiveModuleResponse
import com.dox.adapter.`in`.rest.dto.billing.ModuleAccessResponse
import com.dox.adapter.`in`.rest.dto.billing.ModuleCatalogResponse
import com.dox.adapter.`in`.rest.resource.ModuleResource
import com.dox.application.port.input.ModuleAccessUseCase
import com.dox.shared.ContextHolder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ModuleResourceImpl(
    private val moduleAccessUseCase: ModuleAccessUseCase,
) : ModuleResource {
    override fun catalog(): ResponseEntity<List<ModuleCatalogResponse>> =
        responseEntity(
            moduleAccessUseCase.catalog().map {
                ModuleCatalogResponse(
                    id = it.id,
                    displayName = it.displayName,
                    basePriceMonthlyCents = it.basePriceMonthlyCents,
                    dependencies = it.dependencies,
                    gracePeriodDays = it.gracePeriodDays,
                    gracefulDegradation = it.gracefulDegradation.name,
                )
            },
        )

    override fun active(): ResponseEntity<List<ActiveModuleResponse>> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        return responseEntity(
            moduleAccessUseCase.getActiveModules(tenantId).map {
                ActiveModuleResponse(id = it.id, displayName = it.displayName)
            },
        )
    }

    override fun accessible(): ResponseEntity<List<ModuleAccessResponse>> {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        return responseEntity(
            moduleAccessUseCase.getAccessibleModules(tenantId).map { (module, level) ->
                ModuleAccessResponse(
                    id = module.id,
                    displayName = module.displayName,
                    accessLevel = level.name,
                )
            },
        )
    }
}
