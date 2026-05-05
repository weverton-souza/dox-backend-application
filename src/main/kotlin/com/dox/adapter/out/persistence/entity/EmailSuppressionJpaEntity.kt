package com.dox.adapter.out.persistence.entity

import com.dox.domain.email.EmailSuppressionReason
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "email_suppression", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class EmailSuppressionJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "email", nullable = false)
    var email: String = "",
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    var reason: EmailSuppressionReason = EmailSuppressionReason.MANUAL,
    @Column(name = "notes", columnDefinition = "TEXT")
    var notes: String? = null,
    @CreatedDate
    @Column(name = "suppressed_at", nullable = false, updatable = false)
    var suppressedAt: LocalDateTime? = null,
)
