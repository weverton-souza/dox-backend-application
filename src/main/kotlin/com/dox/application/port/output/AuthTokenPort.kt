package com.dox.application.port.output

import java.util.UUID

interface AuthTokenPort {
    fun generateAccessToken(userId: UUID, email: String, tenantId: UUID): String
    fun generateRefreshToken(): String
    fun validateAccessToken(token: String): Boolean
    fun extractUserId(token: String): UUID
    fun extractEmail(token: String): String
    fun extractTenantId(token: String): UUID
}
