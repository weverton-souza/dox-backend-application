package com.dox.adapter.out.persistence.entity

import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "form_drafts")
@EntityListeners(AuditingEntityListener::class)
class FormDraftJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "form_link_id", nullable = false, unique = true)
    var formLinkId: UUID = UUID.randomUUID(),
    @Type(JsonType::class)
    @Column(name = "partial_response", columnDefinition = "jsonb")
    var partialResponse: Map<String, Any?> = emptyMap(),
    @LastModifiedDate
    @Column(name = "saved_at")
    var savedAt: LocalDateTime? = null,
)
