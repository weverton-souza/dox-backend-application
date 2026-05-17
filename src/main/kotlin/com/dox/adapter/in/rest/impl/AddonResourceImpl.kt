package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.billing.AddonResponse
import com.dox.adapter.`in`.rest.resource.AddonResource
import com.dox.application.port.input.AddonUseCase
import com.dox.domain.billing.Addon
import com.dox.domain.exception.ResourceNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AddonResourceImpl(
    private val addonUseCase: AddonUseCase,
) : AddonResource {
    override fun list(): ResponseEntity<List<AddonResponse>> = responseEntity(addonUseCase.listActive().map { it.toResponse() })

    override fun getById(id: String): ResponseEntity<AddonResponse> {
        val addon = addonUseCase.getById(id) ?: throw ResourceNotFoundException("Addon", id)
        return responseEntity(addon.toResponse())
    }

    private fun Addon.toResponse() =
        AddonResponse(
            id = id,
            name = name,
            description = description,
            type = type,
            targetModuleId = targetModuleId,
            priceMonthlyCents = priceMonthlyCents,
            priceUnitCents = priceUnitCents,
            feePercentage = feePercentage,
            availableForBundles = availableForBundles,
            sortOrder = sortOrder,
        )
}
