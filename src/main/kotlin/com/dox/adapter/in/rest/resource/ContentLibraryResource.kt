package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.contentlibrary.ContentLibraryRequest
import com.dox.adapter.`in`.rest.dto.contentlibrary.ContentLibraryResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Tag(name = "Biblioteca de Conteúdo", description = "CRUD de referências, instrumentos e trechos reutilizáveis")
@RequestMapping("/content-library")
interface ContentLibraryResource : BaseResource {
    @Operation(summary = "Listar conteúdos com filtros opcionais")
    @GetMapping
    fun findAll(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) type: String?
    ): ResponseEntity<List<ContentLibraryResponse>>

    @Operation(summary = "Criar conteúdo")
    @PostMapping
    fun create(
        @Valid @RequestBody request: ContentLibraryRequest
    ): ResponseEntity<ContentLibraryResponse>

    @Operation(summary = "Atualizar conteúdo")
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ContentLibraryRequest
    ): ResponseEntity<ContentLibraryResponse>

    @Operation(summary = "Excluir conteúdo")
    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: UUID
    ): ResponseEntity<Void>
}
