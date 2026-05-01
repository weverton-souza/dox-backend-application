package com.dox.application.port.output

import com.dox.domain.model.AdminUser
import java.time.LocalDateTime
import java.util.UUID

interface AdminUserPersistencePort {
    fun findByEmail(email: String): AdminUser?

    fun findById(id: UUID): AdminUser?

    fun save(adminUser: AdminUser): AdminUser

    fun updateLastLogin(
        id: UUID,
        loginAt: LocalDateTime,
    )
}
