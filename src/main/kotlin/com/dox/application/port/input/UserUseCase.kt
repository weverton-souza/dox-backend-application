package com.dox.application.port.input

import java.util.UUID

data class UserInfo(
    val id: UUID,
    val email: String,
    val name: String,
    val personalTenantId: UUID?
)

data class UpdateUserCommand(
    val userId: UUID,
    val name: String
)

interface UserUseCase {
    fun getMe(userId: UUID): UserInfo

    fun updateMe(command: UpdateUserCommand): UserInfo
}
