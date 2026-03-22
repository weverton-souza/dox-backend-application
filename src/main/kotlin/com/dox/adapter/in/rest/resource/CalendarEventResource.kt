package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.calendar.CalendarEventRequest
import com.dox.adapter.`in`.rest.dto.calendar.CalendarEventResponse
import jakarta.validation.Valid
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
import java.util.UUID

@Tag(name = "Eventos do Calendário", description = "CRUD de eventos do calendário")
@RequestMapping("/calendar-events")
interface CalendarEventResource : BaseResource {

    @Operation(summary = "Listar eventos por período")
    @GetMapping
    fun findByDateRange(
        @RequestParam from: OffsetDateTime,
        @RequestParam to: OffsetDateTime
    ): ResponseEntity<List<CalendarEventResponse>>

    @Operation(summary = "Criar evento")
    @PostMapping
    fun create(@Valid @RequestBody request: CalendarEventRequest): ResponseEntity<CalendarEventResponse>

    @Operation(summary = "Atualizar evento")
    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody request: CalendarEventRequest): ResponseEntity<CalendarEventResponse>

    @Operation(summary = "Excluir evento")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void>
}
