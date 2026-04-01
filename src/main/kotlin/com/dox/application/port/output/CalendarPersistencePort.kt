package com.dox.application.port.output

import com.dox.domain.model.CalendarEvent
import com.dox.domain.model.EventTag
import java.time.OffsetDateTime
import java.util.UUID

interface CalendarPersistencePort {
    fun saveTag(tag: EventTag): EventTag

    fun findAllTags(): List<EventTag>

    fun findTagById(id: UUID): EventTag?

    fun deleteTag(id: UUID)

    fun saveEvent(event: CalendarEvent): CalendarEvent

    fun findEventById(id: UUID): CalendarEvent?

    fun findEventsByDateRange(
        from: OffsetDateTime,
        to: OffsetDateTime,
    ): List<CalendarEvent>

    fun deleteEvent(id: UUID)
}
