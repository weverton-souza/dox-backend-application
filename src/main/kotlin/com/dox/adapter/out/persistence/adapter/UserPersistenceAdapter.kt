package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.TenantJpaEntity
import com.dox.adapter.out.persistence.entity.UserJpaEntity
import com.dox.adapter.out.persistence.repository.UserJpaRepository
import com.dox.application.port.output.UserPersistencePort
import com.dox.domain.model.User
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository
) : UserPersistencePort {
    override fun save(user: User): User {
        val entity = userJpaRepository.findByEmail(user.email) ?: UserJpaEntity()
        entity.email = user.email
        entity.name = user.name
        entity.passwordHash = user.passwordHash
        if (user.personalTenantId != null) {
            entity.personalTenant = TenantJpaEntity().apply { id = user.personalTenantId }
        }
        if (user.id != entity.id) entity.id = user.id
        return userJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: UUID): User? =
        userJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findByEmail(email: String): User? =
        userJpaRepository.findByEmail(email)?.toDomain()

    override fun existsByEmail(email: String): Boolean =
        userJpaRepository.existsByEmail(email)

    private fun UserJpaEntity.toDomain() = User(
        id = id,
        email = email,
        name = name,
        passwordHash = passwordHash,
        personalTenantId = personalTenant?.id,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
