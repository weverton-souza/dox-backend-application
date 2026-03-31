package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.formlink.PublicFormResponse
import com.dox.adapter.`in`.rest.dto.formlink.PublicFormSubmitRequest
import com.dox.adapter.`in`.rest.dto.formlink.PublicFormSubmitResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Formulários Públicos", description = "Endpoints públicos para preenchimento de formulários")
@RequestMapping("/public/forms")
interface PublicFormResource : BaseResource {
    @Operation(summary = "Obter formulário público pelo token")
    @GetMapping("/{token}")
    fun getForm(
        @PathVariable token: String
    ): ResponseEntity<PublicFormResponse>

    @Operation(summary = "Submeter respostas do formulário público")
    @PostMapping("/{token}/submit")
    fun submitForm(
        @PathVariable token: String,
        @Valid @RequestBody request: PublicFormSubmitRequest
    ): ResponseEntity<PublicFormSubmitResponse>
}
