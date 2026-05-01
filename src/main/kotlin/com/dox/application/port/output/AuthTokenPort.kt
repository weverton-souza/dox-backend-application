package com.dox.application.port.output

import com.dox.domain.enum.AdminRole
import java.time.LocalDateTime
import java.util.UUID

data class FormLinkTokenData(val tenantId: UUID, val formLinkId: UUID)

interface AuthTokenPort {
    fun generateAccessToken(
        userId: UUID,
        email: String,
        tenantId: UUID,
    ): String

    fun generateRefreshToken(): String

    fun validateAccessToken(token: String): Boolean

    fun extractUserId(token: String): UUID

    fun extractEmail(token: String): String

    fun extractTenantId(token: String): UUID

    fun generateFormLinkToken(
        tenantId: UUID,
        formLinkId: UUID,
        expiresAt: LocalDateTime,
    ): String

    fun extractFormLinkData(token: String): FormLinkTokenData

    fun generateAdminAccessToken(
        adminId: UUID,
        email: String,
        role: AdminRole,
    ): String

    fun isAdminToken(token: String): Boolean

    fun extractAdminRole(token: String): AdminRole
}
