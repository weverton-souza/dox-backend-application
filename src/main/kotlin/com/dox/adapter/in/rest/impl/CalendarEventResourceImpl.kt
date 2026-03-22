package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.calendar.*
import com.dox.adapter.`in`.rest.resource.CalendarEventResource
import com.dox.application.port.input.*
import com.dox.domain.model.CalendarEvent
import com.dox.domain.model.EventTag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.util.UUID

@RestController
class CalendarEventResourceImpl(
    private val calendarUseCase: CalendarUseCase
) : CalendarEventResource {

    override fun findByDateRange(from: OffsetDateTime, to: OffsetDateTime): ResponseEntity<List<CalendarEventResponse>> {
        val enrichedEvents = calendarUseCase.findEnrichedEventsByDateRange(from, to)
        return responseEntity(enrichedEvents.map { it.toResponse() })
    }

    override fun create(request: CalendarEventRequest): ResponseEntity<CalendarEventResponse> {
        val event = calendarUseCase.createEvent(request.toCommand())
        val enriched = calendarUseCase.enrichEvent(event)
        return responseEntity(enriched.toResponse(), HttpStatus.CREATED)
    }

    override fun update(id: UUID, request: CalendarEventRequest): ResponseEntity<CalendarEventResponse> {
        val event = calendarUseCase.updateEvent(request.toUpdateCommand(id))
        val enriched = calendarUseCase.enrichEvent(event)
        return responseEntity(enriched.toResponse())
    }

    override fun delete(id: UUID): ResponseEntity<Void> {
        calendarUseCase.deleteEvent(id)
        return noContent()
    }

    private fun CalendarEventRequest.toCommand() = CreateCalendarEventCommand(
        summary = summary,
        description = description,
        location = location,
        startDate = start.date,
        startDateTime = start.dateTime,
        startTimeZone = start.timeZone,
        endDate = end.date,
        endDateTime = end.dateTime,
        endTimeZone = end.timeZone,
        allDay = allDay,
        tagId = tagId,
        customerId = customerId,
        status = status
    )

    private fun CalendarEventRequest.toUpdateCommand(id: UUID) = UpdateCalendarEventCommand(
        id = id,
        summary = summary,
        description = description,
        location = location,
        startDate = start.date,
        startDateTime = start.dateTime,
        startTimeZone = start.timeZone,
        endDate = end.date,
        endDateTime = end.dateTime,
        endTimeZone = end.timeZone,
        allDay = allDay,
        tagId = tagId,
        customerId = customerId,
        status = status
    )

    private fun EnrichedCalendarEvent.toResponse(): CalendarEventResponse {
        val e = event
        val t = tag
        return CalendarEventResponse(
            id = e.id,
            summary = e.summary,
            description = e.description,
            location = e.location,
            start = EventDateTimeResponse(e.startDate, e.startDateTime, e.startTimeZone),
            end = EventDateTimeResponse(e.endDate, e.endDateTime, e.endTimeZone),
            allDay = e.allDay,
            tagId = e.tagId,
            tag = t?.let { EventTagResponse(it.id, it.name, it.color, it.createdAt, it.updatedAt) },
            customerId = e.customerId,
            customerName = customerName,
            status = e.status,
            recurrence = e.recurrence,
            reminders = e.reminders,
            googleEventId = e.googleEventId,
            iCalUID = e.iCalUID,
            createdAt = e.createdAt,
            updatedAt = e.updatedAt
        )
    }
}
