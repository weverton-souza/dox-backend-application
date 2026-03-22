package com.dox.adapter.`in`.rest.dto.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val email: String,
    val name: String,
    val personalTenantId: UUID?
)

data class UpdateUserRequest(
    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    val name: String
)
