package com.dox.application.port.input

import com.dox.domain.model.CalendarEvent
import com.dox.domain.model.EventTag
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

data class CreateTagCommand(val name: String, val color: String)
data class UpdateTagCommand(val id: UUID, val name: String, val color: String)

data class CreateCalendarEventCommand(
    val summary: String,
    val description: String?,
    val location: String?,
    val startDate: LocalDate?,
    val startDateTime: OffsetDateTime?,
    val startTimeZone: String?,
    val endDate: LocalDate?,
    val endDateTime: OffsetDateTime?,
    val endTimeZone: String?,
    val allDay: Boolean,
    val tagId: UUID?,
    val customerId: UUID?,
    val status: String
)

data class UpdateCalendarEventCommand(
    val id: UUID,
    val summary: String,
    val description: String?,
    val location: String?,
    val startDate: LocalDate?,
    val startDateTime: OffsetDateTime?,
    val startTimeZone: String?,
    val endDate: LocalDate?,
    val endDateTime: OffsetDateTime?,
    val endTimeZone: String?,
    val allDay: Boolean,
    val tagId: UUID?,
    val customerId: UUID?,
    val status: String
)

data class EnrichedCalendarEvent(
    val event: CalendarEvent,
    val tag: EventTag? = null,
    val customerName: String? = null
)

interface CalendarUseCase {
    fun createTag(command: CreateTagCommand): EventTag
    fun findAllTags(): List<EventTag>
    fun updateTag(command: UpdateTagCommand): EventTag
    fun deleteTag(id: UUID)

    fun createEvent(command: CreateCalendarEventCommand): CalendarEvent
    fun findEventById(id: UUID): CalendarEvent
    fun findEventsByDateRange(from: OffsetDateTime, to: OffsetDateTime): List<CalendarEvent>
    fun findEnrichedEventsByDateRange(from: OffsetDateTime, to: OffsetDateTime): List<EnrichedCalendarEvent>
    fun enrichEvent(event: CalendarEvent): EnrichedCalendarEvent
    fun updateEvent(command: UpdateCalendarEventCommand): CalendarEvent
    fun deleteEvent(id: UUID)
}
