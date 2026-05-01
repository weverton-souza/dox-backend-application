package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.AdminUserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.UUID

interface AdminUserJpaRepository : JpaRepository<AdminUserJpaEntity, UUID> {
    fun findByEmail(email: String): AdminUserJpaEntity?

    @Modifying
    @Query("UPDATE AdminUserJpaEntity a SET a.lastLoginAt = :loginAt WHERE a.id = :id")
    fun updateLastLogin(
        @Param("id") id: UUID,
        @Param("loginAt") loginAt: LocalDateTime,
    )
}
