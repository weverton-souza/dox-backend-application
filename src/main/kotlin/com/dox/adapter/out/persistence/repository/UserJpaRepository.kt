package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UserJpaRepository : JpaRepository<UserJpaEntity, UUID> {
    fun findByEmail(email: String): UserJpaEntity?

    @Query("SELECT u FROM UserJpaEntity u WHERE u.personalTenant.id = :tenantId")
    fun findByPersonalTenantId(
        @Param("tenantId") tenantId: UUID,
    ): UserJpaEntity?

    fun existsByEmail(email: String): Boolean
}
