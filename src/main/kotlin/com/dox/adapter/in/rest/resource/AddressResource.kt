package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.address.AddressResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Endereço", description = "Consulta de endereço por CEP")
@RequestMapping("/public/address")
interface AddressResource : BaseResource {
    @Operation(summary = "Buscar endereço por CEP")
    @GetMapping("/lookup/{cep}")
    fun lookup(
        @PathVariable cep: String,
    ): ResponseEntity<AddressResponse>
}
