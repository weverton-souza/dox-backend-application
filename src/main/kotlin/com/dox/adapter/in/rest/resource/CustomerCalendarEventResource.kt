package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.customer.CustomerCalendarEventResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.OffsetDateTime

@Tag(name = "Eventos do Calendário (Prontuário)", description = "Eventos de prontuário para visualização no calendário")
@RequestMapping("/events")
interface CustomerCalendarEventResource : BaseResource {
    @Operation(summary = "Listar eventos de prontuário por período")
    @GetMapping
    fun findByDateRange(
        @RequestParam from: OffsetDateTime,
        @RequestParam to: OffsetDateTime,
    ): ResponseEntity<List<CustomerCalendarEventResponse>>
}
