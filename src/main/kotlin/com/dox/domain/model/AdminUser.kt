package com.dox.domain.model

import com.dox.domain.enum.AdminRole
import java.time.LocalDateTime
import java.util.UUID

data class AdminUser(
    val id: UUID = UUID.randomUUID(),
    val email: String,
    val name: String,
    val passwordHash: String,
    val role: AdminRole,
    val lastLoginAt: LocalDateTime? = null,
    val createdAt: LocalDateTime? = null,
    val deactivatedAt: LocalDateTime? = null,
) {
    val isActive: Boolean get() = deactivatedAt == null
}
