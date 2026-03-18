package com.dox.adapter.`in`.rest.dto.calendar

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.UUID

data class EventTagRequest(
    val name: String,
    val color: String
)

data class EventTagResponse(
    val id: UUID,
    val name: String,
    val color: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class EventDateTimeRequest(
    val date: LocalDate? = null,
    val dateTime: OffsetDateTime? = null,
    val timeZone: String? = null
)

data class EventDateTimeResponse(
    val date: LocalDate? = null,
    val dateTime: OffsetDateTime? = null,
    val timeZone: String? = null
)

data class CalendarEventRequest(
    val summary: String,
    val description: String? = null,
    val location: String? = null,
    val start: EventDateTimeRequest,
    val end: EventDateTimeRequest,
    val allDay: Boolean = false,
    val tagId: UUID? = null,
    val customerId: UUID? = null,
    val status: String = "confirmed"
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
    val updatedAt: LocalDateTime?
)
