package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.calendar.*
import com.dox.adapter.`in`.rest.resource.CalendarEventResource
import com.dox.application.port.input.*
import com.dox.application.port.output.CalendarPersistencePort
import com.dox.application.port.output.CustomerPersistencePort
import com.dox.domain.model.CalendarEvent
import com.dox.domain.model.EventTag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.util.UUID

@RestController
class CalendarEventResourceImpl(
    private val calendarUseCase: CalendarUseCase,
    private val calendarPersistencePort: CalendarPersistencePort,
    private val customerPersistencePort: CustomerPersistencePort
) : CalendarEventResource {

    override fun findByDateRange(from: OffsetDateTime, to: OffsetDateTime): ResponseEntity<List<CalendarEventResponse>> {
        val events = calendarUseCase.findEventsByDateRange(from, to)
        val tags = calendarPersistencePort.findAllTags().associateBy { it.id }
        val customerIds = events.mapNotNull { it.customerId }.toSet()
        val customers = if (customerIds.isNotEmpty())
            customerPersistencePort.findByIds(customerIds).associate { it.id to (it.data["name"] as? String) }
        else emptyMap()
        return responseEntity(events.map { it.toResponse(tags, customers) })
    }

    override fun create(request: CalendarEventRequest): ResponseEntity<CalendarEventResponse> {
        val event = calendarUseCase.createEvent(request.toCommand())
        val tags = calendarPersistencePort.findAllTags().associateBy { it.id }
        val customers = enrichCustomerName(event)
        return responseEntity(event.toResponse(tags, customers), HttpStatus.CREATED)
    }

    override fun update(id: UUID, request: CalendarEventRequest): ResponseEntity<CalendarEventResponse> {
        val event = calendarUseCase.updateEvent(request.toUpdateCommand(id))
        val tags = calendarPersistencePort.findAllTags().associateBy { it.id }
        val customers = enrichCustomerName(event)
        return responseEntity(event.toResponse(tags, customers))
    }

    override fun delete(id: UUID): ResponseEntity<Void> {
        calendarUseCase.deleteEvent(id)
        return noContent()
    }

    private fun enrichCustomerName(event: CalendarEvent): Map<UUID, String?> {
        val customerId = event.customerId ?: return emptyMap()
        val customer = customerPersistencePort.findById(customerId) ?: return emptyMap()
        return mapOf(customerId to (customer.data["name"] as? String))
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

    private fun CalendarEvent.toResponse(
        tags: Map<UUID, EventTag>,
        customers: Map<UUID, String?>
    ) = CalendarEventResponse(
        id = id,
        summary = summary,
        description = description,
        location = location,
        start = EventDateTimeResponse(startDate, startDateTime, startTimeZone),
        end = EventDateTimeResponse(endDate, endDateTime, endTimeZone),
        allDay = allDay,
        tagId = tagId,
        tag = tagId?.let { tags[it]?.let { t -> EventTagResponse(t.id, t.name, t.color, t.createdAt, t.updatedAt) } },
        customerId = customerId,
        customerName = customerId?.let { customers[it] },
        status = status,
        recurrence = recurrence,
        reminders = reminders,
        googleEventId = googleEventId,
        iCalUID = iCalUID,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
