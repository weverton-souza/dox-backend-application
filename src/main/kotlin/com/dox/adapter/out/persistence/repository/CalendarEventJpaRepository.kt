package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.CalendarEventJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.OffsetDateTime
import java.util.UUID

interface CalendarEventJpaRepository : JpaRepository<CalendarEventJpaEntity, UUID> {
    fun findByStartDateTimeBetweenOrderByStartDateTimeAsc(
        from: OffsetDateTime,
        to: OffsetDateTime,
    ): List<CalendarEventJpaEntity>
}
