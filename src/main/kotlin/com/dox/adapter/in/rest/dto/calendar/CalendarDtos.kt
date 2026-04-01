package com.dox.adapter.`in`.rest.dto.calendar

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.UUID

data class EventTagRequest(
    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    val name: String,
    @field:NotBlank(message = "Cor é obrigatória")
    @field:Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Cor deve ser um código hexadecimal válido (ex: #FF5733)")
    val color: String,
)

data class EventTagResponse(
    val id: UUID,
    val name: String,
    val color: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)

data class EventDateTimeRequest(
    val date: LocalDate? = null,
    val dateTime: OffsetDateTime? = null,
    val timeZone: String? = null,
)

data class EventDateTimeResponse(
    val date: LocalDate? = null,
    val dateTime: OffsetDateTime? = null,
    val timeZone: String? = null,
)

data class CalendarEventRequest(
    @field:NotBlank(message = "Resumo é obrigatório")
    @field:Size(max = 255, message = "Resumo deve ter no máximo 255 caracteres")
    val summary: String,
    @field:Size(max = 2000, message = "Descrição deve ter no máximo 2000 caracteres")
    val description: String? = null,
    @field:Size(max = 500, message = "Local deve ter no máximo 500 caracteres")
    val location: String? = null,
    @field:NotNull(message = "Data de início é obrigatória")
    @field:Valid
    val start: EventDateTimeRequest,
    @field:NotNull(message = "Data de fim é obrigatória")
    @field:Valid
    val end: EventDateTimeRequest,
    val allDay: Boolean = false,
    val tagId: UUID? = null,
    val customerId: UUID? = null,
    @field:Pattern(regexp = "^(tentative|confirmed|cancelled)$", message = "Status deve ser tentative, confirmed ou cancelled")
    val status: String = "confirmed",
)

data class CalendarEventResponse(
    val id: UUID,
    val summary: String,
    val description: String?,
    val location: String?,
    val start: EventDateTimeResponse,
    val end: EventDateTimeResponse,
    val allDay: Boolean,
    val tagId: UUID?,
    val tag: EventTagResponse?,
    val customerId: UUID?,
    val customerName: String?,
    val status: String,
    val recurrence: List<String>?,
    val reminders: Map<String, Any?>?,
    val googleEventId: String?,
    val iCalUID: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)
