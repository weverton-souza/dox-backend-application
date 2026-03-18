package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.CalendarEventJpaEntity
import com.dox.adapter.out.persistence.entity.EventTagJpaEntity
import com.dox.adapter.out.persistence.repository.CalendarEventJpaRepository
import com.dox.adapter.out.persistence.repository.EventTagJpaRepository
import com.dox.application.port.output.CalendarPersistencePort
import com.dox.domain.model.CalendarEvent
import com.dox.domain.model.EventTag
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.UUID

@Component
class CalendarPersistenceAdapter(
    private val tagRepository: EventTagJpaRepository,
    private val eventRepository: CalendarEventJpaRepository
) : CalendarPersistencePort {

    override fun saveTag(tag: EventTag): EventTag {
        val entity = tagRepository.findById(tag.id).orElse(null)
            ?: EventTagJpaEntity().apply { id = tag.id }
        entity.name = tag.name
        entity.color = tag.color
        return tagRepository.save(entity).toDomain()
    }

    override fun findAllTags(): List<EventTag> =
        tagRepository.findAll().map { it.toDomain() }

    override fun findTagById(id: UUID): EventTag? =
        tagRepository.findById(id).orElse(null)?.toDomain()

    override fun deleteTag(id: UUID) =
        tagRepository.deleteById(id)

    override fun saveEvent(event: CalendarEvent): CalendarEvent {
        val entity = eventRepository.findById(event.id).orElse(null)
            ?: CalendarEventJpaEntity().apply { id = event.id }
        entity.summary = event.summary
        entity.description = event.description
        entity.location = event.location
        entity.startDate = event.startDate
        entity.startDateTime = event.startDateTime
        entity.startTimeZone = event.startTimeZone
        entity.endDate = event.endDate
        entity.endDateTime = event.endDateTime
        entity.endTimeZone = event.endTimeZone
        entity.allDay = event.allDay
        entity.tagId = event.tagId
        entity.customerId = event.customerId
        entity.status = event.status
        entity.recurrence = event.recurrence
        entity.reminders = event.reminders
        entity.googleEventId = event.googleEventId
        entity.iCalUID = event.iCalUID
        return eventRepository.save(entity).toDomain()
    }

    override fun findEventById(id: UUID): CalendarEvent? =
        eventRepository.findById(id).orElse(null)?.toDomain()

    override fun findEventsByDateRange(from: OffsetDateTime, to: OffsetDateTime): List<CalendarEvent> =
        eventRepository.findByStartDateTimeBetweenOrderByStartDateTimeAsc(from, to).map { it.toDomain() }

    override fun deleteEvent(id: UUID) =
        eventRepository.deleteById(id)

    private fun EventTagJpaEntity.toDomain() = EventTag(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun CalendarEventJpaEntity.toDomain() = CalendarEvent(
        id = id,
        summary = summary,
        description = description,
        location = location,
        startDate = startDate,
        startDateTime = startDateTime,
        startTimeZone = startTimeZone,
        endDate = endDate,
        endDateTime = endDateTime,
        endTimeZone = endTimeZone,
        allDay = allDay,
        tagId = tagId,
        customerId = customerId,
        status = status,
        recurrence = recurrence,
        reminders = reminders,
        googleEventId = googleEventId,
        iCalUID = iCalUID,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
