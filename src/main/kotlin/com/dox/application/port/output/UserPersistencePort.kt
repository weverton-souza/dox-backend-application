package com.dox.application.port.output

import com.dox.domain.model.User
import java.util.UUID

interface UserPersistencePort {
    fun save(user: User): User

    fun findById(id: UUID): User?

    fun findByEmail(email: String): User?

    fun findByPersonalTenantId(tenantId: UUID): User?

    fun existsByEmail(email: String): Boolean
}
