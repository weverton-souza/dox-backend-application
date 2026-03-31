package com.dox.adapter.out.persistence.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "reference_entries")
@EntityListeners(AuditingEntityListener::class)
class ReferenceEntryJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    var text: String = "",

    @Column(name = "instrument", length = 200)
    var instrument: String? = null,

    @Column(name = "authors", length = 500)
    var authors: String? = null,

    @Column(name = "year")
    var year: Int? = null,

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
)
