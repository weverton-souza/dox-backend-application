package com.dox.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.UUID

data class CalendarEvent(
    val id: UUID = UUID.randomUUID(),
    val summary: String,
    val description: String? = null,
    val location: String? = null,
    val startDate: LocalDate? = null,
    val startDateTime: OffsetDateTime? = null,
    val startTimeZone: String? = null,
    val endDate: LocalDate? = null,
    val endDateTime: OffsetDateTime? = null,
    val endTimeZone: String? = null,
    val allDay: Boolean = false,
    val tagId: UUID? = null,
    val customerId: UUID? = null,
    val status: String = "confirmed",
    val recurrence: List<String>? = null,
    val reminders: Map<String, Any?>? = null,
    val googleEventId: String? = null,
    val iCalUID: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
