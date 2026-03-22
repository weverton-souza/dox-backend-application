package com.dox.adapter.`in`.rest.dto.auth

import com.dox.domain.enum.Vertical
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class RegisterRequest(
    @field:NotBlank(message = "Email é obrigatório")
    @field:Email(message = "Email inválido")
    val email: String,

    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    val name: String,

    @field:NotBlank(message = "Senha é obrigatória")
    @field:Size(min = 8, max = 72, message = "Senha deve ter entre 8 e 72 caracteres")
    val password: String,

    val vertical: Vertical = Vertical.GENERAL
)

data class LoginRequest(
    @field:NotBlank(message = "Email é obrigatório")
    @field:Email(message = "Email inválido")
    val email: String,

    @field:NotBlank(message = "Senha é obrigatória")
    val password: String
)

data class RefreshRequest(
    @field:NotBlank(message = "Refresh token é obrigatório")
    val refreshToken: String
)

data class SwitchTenantRequest(
    @field:NotNull(message = "Tenant ID é obrigatório")
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
