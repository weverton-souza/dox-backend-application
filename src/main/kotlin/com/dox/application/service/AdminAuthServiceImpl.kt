package com.dox.application.service

import com.dox.application.port.input.AdminAuthResult
import com.dox.application.port.input.AdminAuthUseCase
import com.dox.application.port.input.AdminLoginCommand
import com.dox.application.port.output.AdminUserPersistencePort
import com.dox.application.port.output.AuthTokenPort
import com.dox.application.port.output.PasswordEncoderPort
import com.dox.domain.exception.AccessDeniedException
import com.dox.domain.exception.InvalidCredentialsException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.AdminUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class AdminAuthServiceImpl(
    private val adminUserPersistencePort: AdminUserPersistencePort,
    private val passwordEncoderPort: PasswordEncoderPort,
    private val authTokenPort: AuthTokenPort,
) : AdminAuthUseCase {
    @Transactional
    override fun login(command: AdminLoginCommand): AdminAuthResult {
        val admin =
            adminUserPersistencePort.findByEmail(command.email)
                ?: throw InvalidCredentialsException()

        if (!admin.isActive) {
            throw AccessDeniedException("Conta de administrador desativada")
        }

        if (!passwordEncoderPort.matches(command.password, admin.passwordHash)) {
            throw InvalidCredentialsException()
        }

        adminUserPersistencePort.updateLastLogin(admin.id, LocalDateTime.now())

        val token = authTokenPort.generateAdminAccessToken(admin.id, admin.email, admin.role)

        return AdminAuthResult(
            accessToken = token,
            adminId = admin.id,
            email = admin.email,
            name = admin.name,
            role = admin.role,
        )
    }

    @Transactional(readOnly = true)
    override fun me(adminId: UUID): AdminUser =
        adminUserPersistencePort.findById(adminId)
            ?: throw ResourceNotFoundException("Administrador", adminId.toString())
}
