package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.AdminUserJpaEntity
import com.dox.adapter.out.persistence.repository.AdminUserJpaRepository
import com.dox.application.port.output.AdminUserPersistencePort
import com.dox.domain.model.AdminUser
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Component
class AdminUserPersistenceAdapter(
    private val adminUserJpaRepository: AdminUserJpaRepository,
) : AdminUserPersistencePort {
    override fun findByEmail(email: String): AdminUser? = adminUserJpaRepository.findByEmail(email)?.toDomain()

    override fun findById(id: UUID): AdminUser? = adminUserJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun save(adminUser: AdminUser): AdminUser {
        val entity =
            adminUserJpaRepository.findByEmail(adminUser.email)
                ?: AdminUserJpaEntity(id = adminUser.id)
        entity.email = adminUser.email
        entity.name = adminUser.name
        entity.passwordHash = adminUser.passwordHash
        entity.role = adminUser.role
        entity.lastLoginAt = adminUser.lastLoginAt
        entity.deactivatedAt = adminUser.deactivatedAt
        return adminUserJpaRepository.save(entity).toDomain()
    }

    @Transactional
    override fun updateLastLogin(
        id: UUID,
        loginAt: LocalDateTime,
    ) {
        adminUserJpaRepository.updateLastLogin(id, loginAt)
    }

    private fun AdminUserJpaEntity.toDomain() =
        AdminUser(
            id = id,
            email = email,
            name = name,
            passwordHash = passwordHash,
            role = role,
            lastLoginAt = lastLoginAt,
            createdAt = createdAt,
            deactivatedAt = deactivatedAt,
        )
}
