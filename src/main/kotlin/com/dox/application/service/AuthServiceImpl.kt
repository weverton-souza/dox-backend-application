package com.dox.application.service

import com.dox.application.port.input.AuthResult
import com.dox.application.port.input.AuthUseCase
import com.dox.application.port.input.LoginCommand
import com.dox.application.port.input.RegisterCommand
import com.dox.application.port.input.SwitchTenantCommand
import com.dox.application.port.output.AuthTokenPort
import com.dox.application.port.output.OrganizationPersistencePort
import com.dox.application.port.output.PasswordEncoderPort
import com.dox.application.port.output.RefreshTokenPersistencePort
import com.dox.application.port.output.TenantPersistencePort
import com.dox.application.port.output.UserPersistencePort
import com.dox.domain.enum.TenantType
import com.dox.extensions.isExpired
import com.dox.domain.exception.AccessDeniedException
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.InvalidCredentialsException
import com.dox.domain.exception.InvalidTokenException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.exception.TokenExpiredException
import com.dox.domain.model.RefreshToken
import com.dox.domain.model.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.UUID

@Service
class AuthServiceImpl(
    private val userPersistencePort: UserPersistencePort,
    private val tenantPersistencePort: TenantPersistencePort,
    private val tenantProvisioningService: TenantProvisioningService,
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort,
    private val organizationPersistencePort: OrganizationPersistencePort,
    private val authTokenPort: AuthTokenPort,
    private val passwordEncoderPort: PasswordEncoderPort
) : AuthUseCase {

    @Transactional
    override fun register(command: RegisterCommand): AuthResult {
        validatePassword(command.password)

        if (userPersistencePort.existsByEmail(command.email)) {
            throw BusinessException("Não foi possível completar o registro")
        }

        val tenant = tenantProvisioningService.provisionTenant(
            name = command.name,
            type = TenantType.PERSONAL,
            vertical = command.vertical
        )

        val user = userPersistencePort.save(
            User(
                email = command.email,
                name = command.name,
                passwordHash = passwordEncoderPort.encode(command.password),
                personalTenantId = tenant.id
            )
        )

        return generateAuthResult(user, tenant.id)
    }

    @Transactional
    override fun login(command: LoginCommand): AuthResult {
        val user = userPersistencePort.findByEmail(command.email)
            ?: throw InvalidCredentialsException()

        if (!passwordEncoderPort.matches(command.password, user.passwordHash)) {
            throw InvalidCredentialsException()
        }

        val tenantId = user.personalTenantId
            ?: throw BusinessException("Usuário sem workspace pessoal")

        return generateAuthResult(user, tenantId)
    }

    @Transactional
    override fun refresh(refreshToken: String): AuthResult {
        val tokenHash = hashToken(refreshToken)
        val storedToken = refreshTokenPersistencePort.findByTokenHash(tokenHash)
            ?: throw InvalidTokenException("Refresh token inválido")

        if (storedToken.expiresAt.isExpired()) {
            throw TokenExpiredException()
        }

        val user = userPersistencePort.findById(storedToken.userId)
            ?: throw ResourceNotFoundException("Usuário")

        refreshTokenPersistencePort.deleteByUserId(user.id)

        val tenantId = user.personalTenantId
            ?: throw BusinessException("Usuário sem workspace pessoal")

        return generateAuthResult(user, tenantId)
    }

    @Transactional
    override fun logout(userId: UUID) {
        refreshTokenPersistencePort.deleteByUserId(userId)
    }

    @Transactional(readOnly = true)
    override fun switchTenant(command: SwitchTenantCommand): AuthResult {
        val user = userPersistencePort.findById(command.userId)
            ?: throw ResourceNotFoundException("Usuário", command.userId.toString())

        val tenant = tenantPersistencePort.findById(command.tenantId)
            ?: throw ResourceNotFoundException("Tenant", command.tenantId.toString())

        val hasAccess = user.personalTenantId == command.tenantId ||
            organizationPersistencePort.findMembersByUserId(user.id)
                .any { membership ->
                    val org = organizationPersistencePort.findById(membership.organizationId)
                    org?.tenantId == command.tenantId
                }

        if (!hasAccess) {
            throw AccessDeniedException("Sem acesso a este workspace")
        }

        val accessToken = authTokenPort.generateAccessToken(user.id, user.email, command.tenantId)

        return AuthResult(
            accessToken = accessToken,
            refreshToken = "",
            userId = user.id,
            email = user.email,
            name = user.name,
            tenantId = command.tenantId,
            vertical = tenant.vertical
        )
    }

    private fun generateAuthResult(user: User, tenantId: UUID): AuthResult {
        refreshTokenPersistencePort.deleteByUserId(user.id)

        val tenant = tenantPersistencePort.findById(tenantId)
            ?: throw ResourceNotFoundException("Tenant", tenantId.toString())

        val accessToken = authTokenPort.generateAccessToken(user.id, user.email, tenantId)
        val rawRefreshToken = authTokenPort.generateRefreshToken()
        val tokenHash = hashToken(rawRefreshToken)

        refreshTokenPersistencePort.save(
            RefreshToken(
                userId = user.id,
                tokenHash = tokenHash,
                expiresAt = LocalDateTime.now().plusDays(7)
            )
        )

        return AuthResult(
            accessToken = accessToken,
            refreshToken = rawRefreshToken,
            userId = user.id,
            email = user.email,
            name = user.name,
            tenantId = tenantId,
            vertical = tenant.vertical
        )
    }

    private fun validatePassword(password: String) {
        if (password.length < 8) {
            throw BusinessException("A senha deve ter no mínimo 8 caracteres")
        }
        if (password.length > 72) {
            throw BusinessException("A senha deve ter no máximo 72 caracteres")
        }
        if (!password.any { it.isDigit() }) {
            throw BusinessException("A senha deve conter pelo menos um número")
        }
        if (!password.any { it.isLetter() }) {
            throw BusinessException("A senha deve conter pelo menos uma letra")
        }
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(token.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}
