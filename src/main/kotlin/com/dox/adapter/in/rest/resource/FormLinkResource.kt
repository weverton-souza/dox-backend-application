package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.formlink.CreateFormLinkRequest
import com.dox.adapter.`in`.rest.dto.formlink.FormLinkResponse
import com.dox.adapter.`in`.rest.dto.formlink.MultiSendRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Tag(name = "Links de Formulário", description = "Gerenciamento de links públicos para formulários")
@RequestMapping("/form-links")
interface FormLinkResource : BaseResource {
    @Operation(summary = "Criar link público para formulário (single, cliente)")
    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateFormLinkRequest,
    ): ResponseEntity<FormLinkResponse>

    @Operation(summary = "Enviar formulário para múltiplos respondentes (cliente + contatos)")
    @PostMapping("/multi-send")
    fun multiSend(
        @Valid @RequestBody request: MultiSendRequest,
    ): ResponseEntity<List<FormLinkResponse>>

    @Operation(summary = "Listar links do tenant, opcionalmente filtrado por cliente")
    @GetMapping
    fun findAll(
        @RequestParam(required = false) customerId: UUID?,
    ): ResponseEntity<List<FormLinkResponse>>

    @Operation(summary = "Revogar link")
    @DeleteMapping("/{id}")
    fun revoke(
        @PathVariable id: UUID,
    ): ResponseEntity<Void>
}
