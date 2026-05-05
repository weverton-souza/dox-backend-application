package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.address.AddressResponse
import com.dox.adapter.`in`.rest.resource.AddressResource
import com.dox.application.port.input.AddressLookupUseCase
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.Address
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AddressResourceImpl(
    private val addressLookupUseCase: AddressLookupUseCase,
) : AddressResource {
    override fun lookup(cep: String): ResponseEntity<AddressResponse> {
        val address =
            addressLookupUseCase.byCep(cep)
                ?: throw ResourceNotFoundException("Endereço", cep)
        return responseEntity(address.toResponse())
    }

    private fun Address.toResponse(): AddressResponse =
        AddressResponse(
            zipCode = zipCode,
            street = street,
            neighborhood = neighborhood,
            city = city,
            state = state,
        )
}
