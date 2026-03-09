package com.dox.application.service

import com.dox.adapter.out.tenant.TenantContext
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
import com.dox.application.port.output.TenantProvisioningPort
import com.dox.application.port.output.UserPersistencePort
import com.dox.domain.enum.TenantType
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.exception.UnauthorizedException
import com.dox.domain.model.RefreshToken
import com.dox.domain.model.Tenant
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
    private val tenantProvisioningPort: TenantProvisioningPort,
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort,
    private val organizationPersistencePort: OrganizationPersistencePort,
    private val authTokenPort: AuthTokenPort,
    private val passwordEncoderPort: PasswordEncoderPort
) : AuthUseCase {

    @Transactional
    override fun register(command: RegisterCommand): AuthResult {
        if (userPersistencePort.existsByEmail(command.email)) {
            throw BusinessException("Email já cadastrado")
        }

        val tenantId = UUID.randomUUID()
        val schemaName = TenantContext.convertToSchemaName(tenantId.toString())

        val tenant = tenantPersistencePort.save(
            Tenant(
                id = tenantId,
                schemaName = schemaName,
                type = TenantType.PERSONAL,
                name = command.name,
                vertical = command.vertical
            )
        )

        tenantProvisioningPort.createSchema(schemaName)
        tenantProvisioningPort.runMigrations(schemaName)

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

    @Transactional(readOnly = true)
    override fun login(command: LoginCommand): AuthResult {
        val user = userPersistencePort.findByEmail(command.email)
            ?: throw UnauthorizedException("Credenciais inválidas")

        if (!passwordEncoderPort.matches(command.password, user.passwordHash)) {
            throw UnauthorizedException("Credenciais inválidas")
        }

        val tenantId = user.personalTenantId
            ?: throw BusinessException("Usuário sem workspace pessoal")

        return generateAuthResult(user, tenantId)
    }

    @Transactional
    override fun refresh(refreshToken: String): AuthResult {
        val tokenHash = hashToken(refreshToken)
        val storedToken = refreshTokenPersistencePort.findByTokenHash(tokenHash)
            ?: throw UnauthorizedException("Refresh token inválido")

        if (storedToken.expiresAt.isBefore(LocalDateTime.now())) {
            throw UnauthorizedException("Refresh token expirado")
        }

        val user = userPersistencePort.findById(storedToken.userId)
            ?: throw ResourceNotFoundException("Usuário não encontrado")

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
            ?: throw ResourceNotFoundException("Usuário não encontrado")

        val tenant = tenantPersistencePort.findById(command.tenantId)
            ?: throw ResourceNotFoundException("Tenant não encontrado")

        val hasAccess = user.personalTenantId == command.tenantId ||
            organizationPersistencePort.findMembersByUserId(user.id)
                .any { membership ->
                    val org = organizationPersistencePort.findById(membership.organizationId)
                    org?.tenantId == command.tenantId
                }

        if (!hasAccess) {
            throw UnauthorizedException("Sem acesso a este workspace")
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
        val tenant = tenantPersistencePort.findById(tenantId)
            ?: throw ResourceNotFoundException("Tenant não encontrado")

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

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(token.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}
