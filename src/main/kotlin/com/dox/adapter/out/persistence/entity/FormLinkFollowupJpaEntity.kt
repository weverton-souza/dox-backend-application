package com.dox.adapter.out.persistence.entity

import com.dox.domain.email.FollowupLevel
import com.dox.domain.enum.FormLinkFollowupStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "form_link_followups")
@EntityListeners(AuditingEntityListener::class)
class FormLinkFollowupJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "form_link_id", nullable = false)
    var formLinkId: UUID = UUID.randomUUID(),
    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 20)
    var level: FollowupLevel = FollowupLevel.SOFT,
    @Column(name = "day_offset", nullable = false)
    var dayOffset: Int = 1,
    @Column(name = "scheduled_for", nullable = false)
    var scheduledFor: LocalDateTime = LocalDateTime.now(),
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: FormLinkFollowupStatus = FormLinkFollowupStatus.SCHEDULED,
    @Column(name = "email_log_id")
    var emailLogId: UUID? = null,
    @Column(name = "error_message", columnDefinition = "TEXT")
    var errorMessage: String? = null,
    @Column(name = "sent_at")
    var sentAt: LocalDateTime? = null,
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null,
)
