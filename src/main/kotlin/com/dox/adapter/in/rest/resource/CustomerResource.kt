package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.customer.CustomerEventRequest
import com.dox.adapter.`in`.rest.dto.customer.CustomerEventResponse
import com.dox.adapter.`in`.rest.dto.customer.CustomerNoteRequest
import com.dox.adapter.`in`.rest.dto.customer.CustomerNoteResponse
import com.dox.adapter.`in`.rest.dto.customer.CustomerRequest
import com.dox.adapter.`in`.rest.dto.customer.CustomerResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Tag(name = "Clientes", description = "CRUD de clientes, notas e eventos")
@RequestMapping("/customers")
interface CustomerResource : BaseResource {

    @Operation(summary = "Listar clientes com busca e paginação")
    @GetMapping
    fun findAll(
        @RequestParam(required = false) search: String?,
        pageable: Pageable
    ): ResponseEntity<Page<CustomerResponse>>

    @Operation(summary = "Criar cliente")
    @PostMapping
    fun create(@RequestBody request: CustomerRequest): ResponseEntity<CustomerResponse>

    @Operation(summary = "Buscar cliente por ID")
    @GetMapping("/{id}")
    fun findById(@PathVariable id: UUID): ResponseEntity<CustomerResponse>

    @Operation(summary = "Atualizar cliente")
    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody request: CustomerRequest): ResponseEntity<CustomerResponse>

    @Operation(summary = "Excluir cliente (soft delete)")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void>

    @Operation(summary = "Listar notas do cliente")
    @GetMapping("/{id}/notes")
    fun getNotes(@PathVariable id: UUID): ResponseEntity<List<CustomerNoteResponse>>

    @Operation(summary = "Adicionar nota ao cliente")
    @PostMapping("/{id}/notes")
    fun addNote(@PathVariable id: UUID, @RequestBody request: CustomerNoteRequest): ResponseEntity<CustomerNoteResponse>

    @Operation(summary = "Excluir nota")
    @DeleteMapping("/{id}/notes/{noteId}")
    fun deleteNote(@PathVariable id: UUID, @PathVariable noteId: UUID): ResponseEntity<Void>

    @Operation(summary = "Listar eventos do cliente")
    @GetMapping("/{id}/events")
    fun getEvents(@PathVariable id: UUID): ResponseEntity<List<CustomerEventResponse>>

    @Operation(summary = "Adicionar evento ao cliente")
    @PostMapping("/{id}/events")
    fun addEvent(@PathVariable id: UUID, @RequestBody request: CustomerEventRequest): ResponseEntity<CustomerEventResponse>

    @Operation(summary = "Excluir evento")
    @DeleteMapping("/{id}/events/{eventId}")
    fun deleteEvent(@PathVariable id: UUID, @PathVariable eventId: UUID): ResponseEntity<Void>
}
