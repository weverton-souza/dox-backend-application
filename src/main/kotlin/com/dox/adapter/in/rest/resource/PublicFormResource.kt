package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.formlink.PublicFormDraftRequest
import com.dox.adapter.`in`.rest.dto.formlink.PublicFormDraftResponse
import com.dox.adapter.`in`.rest.dto.formlink.PublicFormResponse
import com.dox.adapter.`in`.rest.dto.formlink.PublicFormSubmitRequest
import com.dox.adapter.`in`.rest.dto.formlink.PublicFormSubmitResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Formulários Públicos", description = "Endpoints públicos para preenchimento de formulários")
@RequestMapping("/public/forms")
interface PublicFormResource : BaseResource {
    @Operation(summary = "Obter formulário público pelo token")
    @GetMapping("/{token}")
    fun getForm(
        @PathVariable @Size(max = 2048, message = "Token inválido") token: String,
    ): ResponseEntity<PublicFormResponse>

    @Operation(summary = "Submeter respostas do formulário público")
    @PostMapping("/{token}/submit")
    fun submitForm(
        @PathVariable @Size(max = 2048, message = "Token inválido") token: String,
        @Valid @RequestBody request: PublicFormSubmitRequest,
    ): ResponseEntity<PublicFormSubmitResponse>

    @Operation(summary = "Obter rascunho do preenchimento (auto-save)")
    @GetMapping("/{token}/draft")
    fun getDraft(
        @PathVariable @Size(max = 2048, message = "Token inválido") token: String,
    ): ResponseEntity<PublicFormDraftResponse>

    @Operation(summary = "Salvar rascunho do preenchimento (auto-save)")
    @PutMapping("/{token}/draft")
    fun saveDraft(
        @PathVariable @Size(max = 2048, message = "Token inválido") token: String,
        @Valid @RequestBody request: PublicFormDraftRequest,
    ): ResponseEntity<PublicFormDraftResponse>
}
