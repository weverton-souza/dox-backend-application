package com.dox.adapter.out.persistence.entity

import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "assessment_entries")
@EntityListeners(AuditingEntityListener::class)
class AssessmentEntryJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "assessment_id", nullable = false)
    var assessmentId: UUID = UUID.randomUUID(),
    @Column(name = "instrument_name", nullable = false)
    var instrumentName: String = "",
    @Column(name = "entry_type", nullable = false)
    var entryType: String = "SIMPLE",
    @Column(name = "order_index", nullable = false)
    var orderIndex: Int = 0,
    @Type(JsonType::class)
    @Column(name = "scores", columnDefinition = "jsonb")
    var scores: List<Map<String, Any?>> = emptyList(),
    @Type(JsonType::class)
    @Column(name = "block", columnDefinition = "jsonb")
    var block: Map<String, Any?>? = null,
    @Column(name = "observations")
    var observations: String? = null,
    @Column(name = "attachment_file_id")
    var attachmentFileId: UUID? = null,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
