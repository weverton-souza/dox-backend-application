package com.dox.adapter.out.persistence.entity

import com.dox.domain.enum.FormLinkStatus
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
@Table(name = "form_links")
@EntityListeners(AuditingEntityListener::class)
class FormLinkJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "form_id", nullable = false)
    var formId: UUID = UUID.randomUUID(),
    @Column(name = "customer_id", nullable = false)
    var customerId: UUID = UUID.randomUUID(),
    @Column(name = "created_by", nullable = false)
    var createdBy: UUID = UUID.randomUUID(),
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    var status: FormLinkStatus = FormLinkStatus.PENDING,
    @Column(name = "expires_at", nullable = false)
    var expiresAt: LocalDateTime = LocalDateTime.now(),
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
)
