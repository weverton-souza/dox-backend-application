package com.dox.application.port.output

import com.dox.domain.model.RefreshToken
import java.util.UUID

interface RefreshTokenPersistencePort {
    fun save(refreshToken: RefreshToken): RefreshToken

    fun findByTokenHash(tokenHash: String): RefreshToken?

    fun deleteByUserId(userId: UUID)

    fun deleteById(id: UUID)

    fun deleteExpired()
}
