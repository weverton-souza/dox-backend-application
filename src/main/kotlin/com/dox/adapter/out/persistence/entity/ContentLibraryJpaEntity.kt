package com.dox.adapter.out.persistence.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "content_library")
@EntityListeners(AuditingEntityListener::class)
class ContentLibraryJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "title", nullable = false, length = 300)
    var title: String = "",
    @Column(name = "content", nullable = false, columnDefinition = "JSONB")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    var content: List<Map<String, Any?>> = emptyList(),
    @Column(name = "type", nullable = false, length = 50)
    var type: String = "reference",
    @Column(name = "category", nullable = false, length = 50)
    var category: String = "general",
    @Column(name = "instrument", length = 200)
    var instrument: String? = null,
    @Column(name = "authors", length = 500)
    var authors: String? = null,
    @Column(name = "year")
    var year: Int? = null,
    @Column(name = "tags", length = 500)
    var tags: String? = null,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
