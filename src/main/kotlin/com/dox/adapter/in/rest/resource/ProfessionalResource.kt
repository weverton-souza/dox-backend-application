package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.professional.ProfessionalRequest
import com.dox.adapter.`in`.rest.dto.professional.ProfessionalResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Profissional", description = "Configurações profissionais do workspace")
@RequestMapping("/professional")
interface ProfessionalResource : BaseResource {

    @Operation(summary = "Obter configurações profissionais")
    @GetMapping
    fun get(): ResponseEntity<ProfessionalResponse>

    @Operation(summary = "Atualizar configurações profissionais")
    @PutMapping
    fun update(@RequestBody request: ProfessionalRequest): ResponseEntity<ProfessionalResponse>
}
