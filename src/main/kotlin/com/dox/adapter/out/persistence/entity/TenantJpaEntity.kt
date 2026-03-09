package com.dox.adapter.out.persistence.entity

import com.dox.domain.enum.TenantType
import com.dox.domain.enum.Vertical
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
@Table(name = "tenants")
@EntityListeners(AuditingEntityListener::class)
class TenantJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "schema_name", unique = true, nullable = false)
    var schemaName: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: TenantType = TenantType.PERSONAL,

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "vertical", nullable = false)
    var vertical: Vertical = Vertical.GENERAL,

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null
)
