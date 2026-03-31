package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.reference.ReferenceEntryRequest
import com.dox.adapter.`in`.rest.dto.reference.ReferenceEntryResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Tag(name = "Referências Bibliográficas", description = "CRUD de referências bibliográficas")
@RequestMapping("/reference-entries")
interface ReferenceEntryResource : BaseResource {

    @Operation(summary = "Listar todas as referências")
    @GetMapping
    fun findAll(@RequestParam(required = false) query: String?): ResponseEntity<List<ReferenceEntryResponse>>

    @Operation(summary = "Criar referência")
    @PostMapping
    fun create(@Valid @RequestBody request: ReferenceEntryRequest): ResponseEntity<ReferenceEntryResponse>

    @Operation(summary = "Atualizar referência")
    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody request: ReferenceEntryRequest): ResponseEntity<ReferenceEntryResponse>

    @Operation(summary = "Excluir referência")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void>
}
