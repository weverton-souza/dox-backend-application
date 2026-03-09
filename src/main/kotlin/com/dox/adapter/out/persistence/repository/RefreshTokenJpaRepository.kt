package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.RefreshTokenJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.UUID

interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenJpaEntity, UUID> {
    fun findByTokenHash(tokenHash: String): RefreshTokenJpaEntity?
    fun deleteByUserId(userId: UUID)
    fun deleteByExpiresAtBefore(dateTime: LocalDateTime)
}
