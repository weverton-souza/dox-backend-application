package com.dox.application.port.input

import com.dox.domain.enum.Vertical
import java.util.UUID

data class RegisterCommand(
    val email: String,
    val name: String,
    val password: String,
    val vertical: Vertical = Vertical.GENERAL
)

data class LoginCommand(
    val email: String,
    val password: String
)

data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val userId: UUID,
    val email: String,
    val name: String,
    val tenantId: UUID,
    val vertical: Vertical
)

data class SwitchTenantCommand(
    val userId: UUID,
    val tenantId: UUID
)

interface AuthUseCase {
    fun register(command: RegisterCommand): AuthResult

    fun login(command: LoginCommand): AuthResult

    fun refresh(refreshToken: String): AuthResult

    fun logout(userId: UUID)

    fun switchTenant(command: SwitchTenantCommand): AuthResult
}
