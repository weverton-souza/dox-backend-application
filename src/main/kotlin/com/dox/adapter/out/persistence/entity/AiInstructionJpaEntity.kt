package com.dox.adapter.out.persistence.entity

import com.dox.domain.enum.Vertical
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
@Table(name = "ai_instructions", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class AiInstructionJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "type", nullable = false)
    var type: String = "",
    @Enumerated(EnumType.STRING)
    @Column(name = "vertical")
    var vertical: Vertical? = null,
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    var content: String = "",
    @Column(name = "active", nullable = false)
    var active: Boolean = true,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
)
