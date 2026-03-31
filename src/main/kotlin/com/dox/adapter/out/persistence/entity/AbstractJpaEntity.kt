package com.dox.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.SQLRestriction
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@MappedSuperclass
@SQLRestriction("deleted = false")
@EntityListeners(AuditingEntityListener::class)
abstract class AbstractJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    open var id: UUID = UUID.randomUUID(),
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    open var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    open var updatedAt: LocalDateTime? = null,
    @Column(name = "deleted")
    open var deleted: Boolean = false
)
