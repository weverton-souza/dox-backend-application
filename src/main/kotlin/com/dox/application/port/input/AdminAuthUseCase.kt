package com.dox.application.port.input

import com.dox.domain.enum.AdminRole
import com.dox.domain.model.AdminUser
import java.util.UUID

data class AdminLoginCommand(
    val email: String,
    val password: String,
)

data class AdminAuthResult(
    val accessToken: String,
    val adminId: UUID,
    val email: String,
    val name: String,
    val role: AdminRole,
)

interface AdminAuthUseCase {
    fun login(command: AdminLoginCommand): AdminAuthResult

    fun me(adminId: UUID): AdminUser
}
