package com.dox.adapter.out.persistence.entity

import com.dox.domain.enum.AdminRole
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
@Table(name = "admin_users", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class AdminUserJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "email", unique = true, nullable = false)
    var email: String = "",
    @Column(name = "password_hash", nullable = false)
    var passwordHash: String = "",
    @Column(name = "name", nullable = false)
    var name: String = "",
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    var role: AdminRole = AdminRole.ADMIN,
    @Column(name = "last_login_at")
    var lastLoginAt: LocalDateTime? = null,
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,
    @Column(name = "deactivated_at")
    var deactivatedAt: LocalDateTime? = null,
)
