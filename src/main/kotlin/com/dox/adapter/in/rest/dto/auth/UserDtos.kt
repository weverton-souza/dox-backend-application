package com.dox.adapter.`in`.rest.dto.auth

import java.util.UUID

data class UserResponse(
    val id: UUID,
    val email: String,
    val name: String,
    val personalTenantId: UUID?
)

data class UpdateUserRequest(
    val name: String
)
