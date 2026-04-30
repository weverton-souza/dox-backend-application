package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.billing.BundleModuleResponse
import com.dox.adapter.`in`.rest.dto.billing.BundleResponse
import com.dox.adapter.`in`.rest.resource.BundleResource
import com.dox.application.port.input.BundleUseCase
import com.dox.domain.billing.Bundle
import com.dox.domain.billing.Module
import com.dox.domain.exception.ResourceNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class BundleResourceImpl(
    private val bundleUseCase: BundleUseCase,
) : BundleResource {
    override fun list(): ResponseEntity<List<BundleResponse>> = responseEntity(bundleUseCase.listActive().map { it.toResponse() })

    override fun getById(id: String): ResponseEntity<BundleResponse> {
        val bundle = bundleUseCase.getById(id) ?: throw ResourceNotFoundException("Bundle", id)
        return responseEntity(bundle.toResponse())
    }

    private fun Bundle.toResponse() =
        BundleResponse(
            id = id,
            name = name,
            description = description,
            modules =
                modules.mapNotNull { moduleId ->
                    Module.fromId(moduleId)?.let {
                        BundleModuleResponse(id = it.id, displayName = it.displayName)
                    }
                },
            priceMonthlyCents = priceMonthlyCents,
            priceYearlyCents = priceYearlyCents,
            seatsIncluded = seatsIncluded,
            trackingSlotsIncluded = trackingSlotsIncluded,
            highlighted = highlighted,
            sortOrder = sortOrder,
        )
}
