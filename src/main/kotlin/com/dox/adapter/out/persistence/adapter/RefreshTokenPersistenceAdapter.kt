package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.RefreshTokenJpaEntity
import com.dox.adapter.out.persistence.repository.RefreshTokenJpaRepository
import com.dox.application.port.output.RefreshTokenPersistencePort
import com.dox.domain.model.RefreshToken
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Component
class RefreshTokenPersistenceAdapter(
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
) : RefreshTokenPersistencePort {
    override fun save(refreshToken: RefreshToken): RefreshToken {
        val entity =
            RefreshTokenJpaEntity().apply {
                id = refreshToken.id
                userId = refreshToken.userId
                tokenHash = refreshToken.tokenHash
                expiresAt = refreshToken.expiresAt
            }
        return refreshTokenJpaRepository.save(entity).toDomain()
    }

    override fun findByTokenHash(tokenHash: String): RefreshToken? = refreshTokenJpaRepository.findByTokenHash(tokenHash)?.toDomain()

    @Transactional
    override fun deleteByUserId(userId: UUID) {
        refreshTokenJpaRepository.deleteByUserId(userId)
    }

    @Transactional
    override fun deleteExpired() {
        refreshTokenJpaRepository.deleteByExpiresAtBefore(LocalDateTime.now())
    }

    private fun RefreshTokenJpaEntity.toDomain() =
        RefreshToken(
            id = id,
            userId = userId,
            tokenHash = tokenHash,
            expiresAt = expiresAt,
            createdAt = createdAt,
        )
}
