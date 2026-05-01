package com.dox.adapter.`in`.rest.dto.admin

import com.dox.domain.enum.AdminRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime
import java.util.UUID

data class AdminLoginRequest(
    @field:NotBlank(message = "Email é obrigatório")
    @field:Email(message = "Email inválido")
    val email: String,
    @field:NotBlank(message = "Senha é obrigatória")
    val password: String,
)

data class AdminAuthResponse(
    val accessToken: String,
    val adminId: UUID,
    val email: String,
    val name: String,
    val role: AdminRole,
)

data class AdminMeResponse(
    val id: UUID,
    val email: String,
    val name: String,
    val role: AdminRole,
    val lastLoginAt: LocalDateTime?,
)
