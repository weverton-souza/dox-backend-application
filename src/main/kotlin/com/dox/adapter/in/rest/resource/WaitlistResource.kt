package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.waitlist.WaitlistRequest
import com.dox.adapter.`in`.rest.dto.waitlist.WaitlistResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Waitlist", description = "Lista de espera pré-lançamento")
@RequestMapping("/public/waitlist")
interface WaitlistResource : BaseResource {
    @Operation(summary = "Entrar na lista de espera", security = [])
    @PostMapping
    fun join(
        @Valid @RequestBody request: WaitlistRequest,
    ): ResponseEntity<WaitlistResponse>
}
