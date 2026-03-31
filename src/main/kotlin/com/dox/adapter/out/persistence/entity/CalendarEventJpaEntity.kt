package com.dox.adapter.out.persistence.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "calendar_events")
@EntityListeners(AuditingEntityListener::class)
class CalendarEventJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "summary", nullable = false, length = 500)
    var summary: String = "",
    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,
    @Column(name = "location", length = 500)
    var location: String? = null,
    @Column(name = "start_date")
    var startDate: LocalDate? = null,
    @Column(name = "start_date_time")
    var startDateTime: OffsetDateTime? = null,
    @Column(name = "start_time_zone", length = 100)
    var startTimeZone: String? = null,
    @Column(name = "end_date")
    var endDate: LocalDate? = null,
    @Column(name = "end_date_time")
    var endDateTime: OffsetDateTime? = null,
    @Column(name = "end_time_zone", length = 100)
    var endTimeZone: String? = null,
    @Column(name = "all_day")
    var allDay: Boolean = false,
    @Column(name = "tag_id")
    var tagId: UUID? = null,
    @Column(name = "customer_id")
    var customerId: UUID? = null,
    @Column(name = "status", length = 20)
    var status: String = "confirmed",
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recurrence", columnDefinition = "jsonb")
    var recurrence: List<String>? = null,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reminders", columnDefinition = "jsonb")
    var reminders: Map<String, Any?>? = null,
    @Column(name = "google_event_id", length = 1024)
    var googleEventId: String? = null,
    @Column(name = "ical_uid", length = 1024)
    var iCalUID: String? = null,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
)
