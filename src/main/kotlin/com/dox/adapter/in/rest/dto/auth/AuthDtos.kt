package com.dox.adapter.`in`.rest.dto.auth

import com.dox.domain.enum.Vertical
import java.util.UUID

data class RegisterRequest(
    val email: String,
    val name: String,
    val password: String,
    val vertical: Vertical = Vertical.GENERAL
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RefreshRequest(
    val refreshToken: String
)

data class SwitchTenantRequest(
    val tenantId: UUID
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: UUID,
    val email: String,
    val name: String,
    val tenantId: UUID,
    val vertical: Vertical
)
