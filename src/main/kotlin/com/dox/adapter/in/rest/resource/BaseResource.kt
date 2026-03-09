package com.dox.adapter.`in`.rest.resource

import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@ApiResponses(
    value = [
        ApiResponse(responseCode = "400", description = "Requisição inválida"),
        ApiResponse(responseCode = "401", description = "Não autenticado"),
        ApiResponse(responseCode = "403", description = "Sem permissão"),
        ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
        ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    ]
)
interface BaseResource {

    fun <T> responseEntity(body: T, status: HttpStatus = HttpStatus.OK): ResponseEntity<T> =
        ResponseEntity.status(status).body(body)

    fun noContent(): ResponseEntity<Void> =
        ResponseEntity.noContent().build()
}
