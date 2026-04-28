package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.verify.PublicVerifyResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Verificação Pública", description = "Validação pública de relatórios finalizados")
@RequestMapping("/public/verify")
interface PublicVerifyResource : BaseResource {
    @Operation(
        summary = "Validar relatório finalizado pelo código",
        security = [],
    )
    @GetMapping("/{code}")
    fun verify(
        @PathVariable code: String,
    ): ResponseEntity<PublicVerifyResponse>
}
