package com.dox.adapter.out.cep

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "viaCepClient", url = "https://viacep.com.br/ws")
interface ViaCepClient {
    @GetMapping("/{cep}/json")
    fun fetch(
        @PathVariable cep: String,
    ): ViaCepResponse
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ViaCepResponse(
    @JsonProperty("cep")
    val zipCode: String? = null,
    @JsonProperty("logradouro")
    val street: String? = null,
    @JsonProperty("bairro")
    val neighborhood: String? = null,
    @JsonProperty("localidade")
    val city: String? = null,
    @JsonProperty("uf")
    val state: String? = null,
    @JsonProperty("erro")
    val error: Boolean? = null,
)
