package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.calendar.EventTagRequest
import com.dox.adapter.`in`.rest.dto.calendar.EventTagResponse
import jakarta.validation.Valid
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Tag(name = "Tags de Evento", description = "CRUD de tags para eventos do calendário")
@RequestMapping("/event-tags")
interface EventTagResource : BaseResource {

    @Operation(summary = "Listar todas as tags")
    @GetMapping
    fun findAll(): ResponseEntity<List<EventTagResponse>>

    @Operation(summary = "Criar tag")
    @PostMapping
    fun create(@Valid @RequestBody request: EventTagRequest): ResponseEntity<EventTagResponse>

    @Operation(summary = "Atualizar tag")
    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody request: EventTagRequest): ResponseEntity<EventTagResponse>

    @Operation(summary = "Excluir tag")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void>
}
