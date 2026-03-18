package com.dox.application.service

import com.dox.application.port.input.*
import com.dox.application.port.output.CalendarPersistencePort
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.CalendarEvent
import com.dox.domain.model.EventTag
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
class CalendarServiceImpl(
    private val calendarPersistencePort: CalendarPersistencePort
) : CalendarUseCase {

    @Transactional
    override fun createTag(command: CreateTagCommand): EventTag =
        calendarPersistencePort.saveTag(EventTag(name = command.name, color = command.color))

    override fun findAllTags(): List<EventTag> =
        calendarPersistencePort.findAllTags()

    @Transactional
    override fun updateTag(command: UpdateTagCommand): EventTag {
        calendarPersistencePort.findTagById(command.id)
            ?: throw ResourceNotFoundException("Tag", command.id.toString())
        return calendarPersistencePort.saveTag(EventTag(id = command.id, name = command.name, color = command.color))
    }

    @Transactional
    override fun deleteTag(id: UUID) {
        calendarPersistencePort.findTagById(id)
            ?: throw ResourceNotFoundException("Tag", id.toString())
        calendarPersistencePort.deleteTag(id)
    }

    @Transactional
    override fun createEvent(command: CreateCalendarEventCommand): CalendarEvent =
        calendarPersistencePort.saveEvent(
            CalendarEvent(
                summary = command.summary,
                description = command.description,
                location = command.location,
                startDate = command.startDate,
                startDateTime = command.startDateTime,
                startTimeZone = command.startTimeZone,
                endDate = command.endDate,
                endDateTime = command.endDateTime,
                endTimeZone = command.endTimeZone,
                allDay = command.allDay,
                tagId = command.tagId,
                customerId = command.customerId,
                status = command.status
            )
        )

    override fun findEventById(id: UUID): CalendarEvent =
        calendarPersistencePort.findEventById(id)
            ?: throw ResourceNotFoundException("Evento", id.toString())

    override fun findEventsByDateRange(from: OffsetDateTime, to: OffsetDateTime): List<CalendarEvent> =
        calendarPersistencePort.findEventsByDateRange(from, to)

    @Transactional
    override fun updateEvent(command: UpdateCalendarEventCommand): CalendarEvent {
        calendarPersistencePort.findEventById(command.id)
            ?: throw ResourceNotFoundException("Evento", command.id.toString())
        return calendarPersistencePort.saveEvent(
            CalendarEvent(
                id = command.id,
                summary = command.summary,
                description = command.description,
                location = command.location,
                startDate = command.startDate,
                startDateTime = command.startDateTime,
                startTimeZone = command.startTimeZone,
                endDate = command.endDate,
                endDateTime = command.endDateTime,
                endTimeZone = command.endTimeZone,
                allDay = command.allDay,
                tagId = command.tagId,
                customerId = command.customerId,
                status = command.status
            )
        )
    }

    @Transactional
    override fun deleteEvent(id: UUID) {
        calendarPersistencePort.findEventById(id)
            ?: throw ResourceNotFoundException("Evento", id.toString())
        calendarPersistencePort.deleteEvent(id)
    }
}
