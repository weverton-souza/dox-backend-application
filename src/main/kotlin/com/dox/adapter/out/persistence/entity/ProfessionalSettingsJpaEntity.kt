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
@Table(name = "professional_settings")
@EntityListeners(AuditingEntityListener::class)
class ProfessionalSettingsJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "name", nullable = false)
    var name: String = "",
    @Column(name = "crp")
    var crp: String? = null,
    @Column(name = "specialization", nullable = false)
    var specialization: String = "",
    @Column(name = "phone")
    var phone: String? = null,
    @Column(name = "instagram")
    var instagram: String? = null,
    @Column(name = "email")
    var email: String? = null,
    @Column(name = "logo", columnDefinition = "TEXT")
    var logo: String? = null,
    @Type(JsonType::class)
    @Column(name = "contact_items", columnDefinition = "jsonb")
    var contactItems: List<Map<String, Any?>> = emptyList(),
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
)
