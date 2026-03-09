package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.form.FormRequest
import com.dox.adapter.`in`.rest.dto.form.FormResponseDto
import com.dox.adapter.`in`.rest.dto.form.FormResponseRequest
import com.dox.adapter.`in`.rest.dto.form.FormResponseResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Tag(name = "Formulários", description = "CRUD de formulários e respostas")
@RequestMapping("/forms")
interface FormResource : BaseResource {

    @Operation(summary = "Listar formulários")
    @GetMapping
    fun findAll(): ResponseEntity<List<FormResponseDto>>

    @Operation(summary = "Criar formulário")
    @PostMapping
    fun create(@RequestBody request: FormRequest): ResponseEntity<FormResponseDto>

    @Operation(summary = "Buscar formulário por ID")
    @GetMapping("/{id}")
    fun findById(@PathVariable id: UUID): ResponseEntity<FormResponseDto>

    @Operation(summary = "Atualizar formulário")
    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody request: FormRequest): ResponseEntity<FormResponseDto>

    @Operation(summary = "Excluir formulário")
    @DeleteMapping("/{id}")
    fun deleteForm(@PathVariable id: UUID): ResponseEntity<Void>

    @Operation(summary = "Listar respostas de um formulário")
    @GetMapping("/{id}/responses")
    fun getResponses(@PathVariable id: UUID): ResponseEntity<List<FormResponseResponseDto>>

    @Operation(summary = "Criar resposta para formulário")
    @PostMapping("/{id}/responses")
    fun createResponse(
        @PathVariable id: UUID,
        @RequestBody request: FormResponseRequest
    ): ResponseEntity<FormResponseResponseDto>

    @Operation(summary = "Buscar resposta por ID")
    @GetMapping("/{id}/responses/{responseId}")
    fun getResponse(
        @PathVariable id: UUID,
        @PathVariable responseId: UUID
    ): ResponseEntity<FormResponseResponseDto>

    @Operation(summary = "Atualizar resposta")
    @PutMapping("/{id}/responses/{responseId}")
    fun updateResponse(
        @PathVariable id: UUID,
        @PathVariable responseId: UUID,
        @RequestBody request: FormResponseRequest
    ): ResponseEntity<FormResponseResponseDto>

    @Operation(summary = "Excluir resposta")
    @DeleteMapping("/{id}/responses/{responseId}")
    fun deleteResponse(@PathVariable id: UUID, @PathVariable responseId: UUID): ResponseEntity<Void>
}
