package com.dox.application.service

import com.dox.application.port.input.AuthResult
import com.dox.application.port.input.AuthUseCase
import com.dox.application.port.input.LoginCommand
import com.dox.application.port.input.RegisterCommand
import com.dox.application.port.input.SendWelcomeEmailCommand
import com.dox.application.port.input.SwitchTenantCommand
import com.dox.application.port.input.VerifyEmailResult
import com.dox.application.port.output.AuthTokenPort
import com.dox.application.port.output.OrganizationPersistencePort
import com.dox.application.port.output.PasswordEncoderPort
import com.dox.application.port.output.RefreshTokenPersistencePort
import com.dox.application.port.output.TenantPersistencePort
import com.dox.application.port.output.UserPersistencePort
import com.dox.domain.enum.TenantType
import com.dox.domain.exception.AccessDeniedException
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.InvalidCredentialsException
import com.dox.domain.exception.InvalidTokenException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.exception.TokenExpiredException
import com.dox.domain.model.RefreshToken
import com.dox.domain.model.User
import com.dox.extensions.isExpired
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionalEventListener
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.Base64
import java.util.UUID

data class WelcomeEmailRequestedEvent(
    val command: SendWelcomeEmailCommand,
)

@Service
class AuthServiceImpl(
    private val userPersistencePort: UserPersistencePort,
    private val tenantPersistencePort: TenantPersistencePort,
    private val tenantProvisioningService: TenantProvisioningService,
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort,
    private val organizationPersistencePort: OrganizationPersistencePort,
    private val authTokenPort: AuthTokenPort,
    private val passwordEncoderPort: PasswordEncoderPort,
    private val customerLabelService: CustomerLabelService,
    private val eventPublisher: ApplicationEventPublisher,
) : AuthUseCase {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val REFRESH_TOKEN_DURATION_DAYS = 7L
        private const val MIN_PASSWORD_LENGTH = 8
        private const val MAX_PASSWORD_LENGTH = 72
        private const val EMAIL_VERIFICATION_TOKEN_BYTES = 32
        private const val EMAIL_VERIFICATION_TOKEN_TTL_HOURS = 72L
    }

    @Transactional
    override fun register(command: RegisterCommand): AuthResult {
        validatePassword(command.password)

        if (userPersistencePort.existsByEmail(command.email)) {
            throw BusinessException("Não foi possível completar o registro")
        }

        val tenant =
            tenantProvisioningService.provisionTenant(
                name = command.name,
                type = TenantType.PERSONAL,
                vertical = command.vertical,
            )

        val verificationToken = generateVerificationToken()
        val tokenExpiresAt = LocalDateTime.now().plusHours(EMAIL_VERIFICATION_TOKEN_TTL_HOURS)

        val user =
            userPersistencePort.save(
                User(
                    email = command.email,
                    name = command.name,
                    passwordHash = passwordEncoderPort.encode(command.password),
                    personalTenantId = tenant.id,
                    emailVerifiedAt = null,
                    emailVerificationToken = verificationToken,
                    emailVerificationTokenExpiresAt = tokenExpiresAt,
                ),
            )

        eventPublisher.publishEvent(
            WelcomeEmailRequestedEvent(
                SendWelcomeEmailCommand(
                    userId = user.id,
                    firstName = user.name.substringBefore(' '),
                    recipient = user.email,
                    verificationToken = verificationToken,
                ),
            ),
        )

        return generateAuthResult(user, tenant.id)
    }

    @Transactional
    override fun login(command: LoginCommand): AuthResult {
        val user =
            userPersistencePort.findByEmail(command.email)
                ?: throw InvalidCredentialsException()

        if (!passwordEncoderPort.matches(command.password, user.passwordHash)) {
            throw InvalidCredentialsException()
        }

        val tenantId =
            user.personalTenantId
                ?: throw BusinessException("Usuário sem workspace pessoal")

        return generateAuthResult(user, tenantId)
    }

    @Transactional
    override fun refresh(refreshToken: String): AuthResult {
        val tokenHash = hashToken(refreshToken)
        val storedToken =
            refreshTokenPersistencePort.findByTokenHash(tokenHash)
                ?: throw InvalidTokenException("Refresh token inválido")

        if (storedToken.expiresAt.isExpired()) {
            throw TokenExpiredException()
        }

        val user =
            userPersistencePort.findById(storedToken.userId)
                ?: throw ResourceNotFoundException("Usuário")

        refreshTokenPersistencePort.deleteById(storedToken.id)

        val tenantId =
            user.personalTenantId
                ?: throw BusinessException("Usuário sem workspace pessoal")

        return generateAuthResult(user, tenantId, deleteOtherTokens = false)
    }

    @Transactional
    override fun logout(userId: UUID) {
        refreshTokenPersistencePort.deleteByUserId(userId)
    }

    @Transactional
    override fun switchTenant(command: SwitchTenantCommand): AuthResult {
        val user =
            userPersistencePort.findById(command.userId)
                ?: throw ResourceNotFoundException("Usuário", command.userId.toString())

        tenantPersistencePort.findById(command.tenantId)
            ?: throw ResourceNotFoundException("Tenant", command.tenantId.toString())

        val hasAccess =
            user.personalTenantId == command.tenantId ||
                organizationPersistencePort.findMembersByUserId(user.id)
                    .any { membership ->
                        val org = organizationPersistencePort.findById(membership.organizationId)
                        org?.tenantId == command.tenantId
                    }

        if (!hasAccess) {
            throw AccessDeniedException("Sem acesso a este workspace")
        }

        return generateAuthResult(user, command.tenantId)
    }

    @Transactional
    override fun verifyEmail(token: String): VerifyEmailResult {
        val user =
            userPersistencePort.findByEmailVerificationToken(token)
                ?: throw InvalidTokenException("Token de verificação inválido")

        if (user.emailVerifiedAt != null) {
            return VerifyEmailResult(verified = true, alreadyVerified = true, email = user.email)
        }

        val expiresAt = user.emailVerificationTokenExpiresAt
        if (expiresAt != null && expiresAt.isExpired()) {
            throw TokenExpiredException()
        }

        userPersistencePort.save(
            user.copy(emailVerifiedAt = LocalDateTime.now()),
        )

        return VerifyEmailResult(verified = true, alreadyVerified = false, email = user.email)
    }

    private fun generateAuthResult(
        user: User,
        tenantId: UUID,
        deleteOtherTokens: Boolean = true,
    ): AuthResult {
        if (deleteOtherTokens) {
            refreshTokenPersistencePort.deleteByUserId(user.id)
        }

        val tenant =
            tenantPersistencePort.findById(tenantId)
                ?: throw ResourceNotFoundException("Tenant", tenantId.toString())

        val accessToken = authTokenPort.generateAccessToken(user.id, user.email, tenantId)
        val rawRefreshToken = authTokenPort.generateRefreshToken()
        val tokenHash = hashToken(rawRefreshToken)

        refreshTokenPersistencePort.save(
            RefreshToken(
                userId = user.id,
                tokenHash = tokenHash,
                expiresAt = LocalDateTime.now().plusDays(REFRESH_TOKEN_DURATION_DAYS),
            ),
        )

        return AuthResult(
            accessToken = accessToken,
            refreshToken = rawRefreshToken,
            userId = user.id,
            email = user.email,
            name = user.name,
            tenantId = tenantId,
            vertical = tenant.vertical,
            emailVerified = user.emailVerifiedAt != null,
            customerLabel = customerLabelService.resolveForTenant(tenantId, tenant.vertical),
        )
    }

    private fun validatePassword(password: String) {
        if (password.length < MIN_PASSWORD_LENGTH) {
            throw BusinessException("A senha deve ter no mínimo $MIN_PASSWORD_LENGTH caracteres")
        }
        if (password.length > MAX_PASSWORD_LENGTH) {
            throw BusinessException("A senha deve ter no máximo $MAX_PASSWORD_LENGTH caracteres")
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

    private fun generateVerificationToken(): String {
        val bytes = ByteArray(EMAIL_VERIFICATION_TOKEN_BYTES)
        SecureRandom().nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}

@org.springframework.stereotype.Component
class WelcomeEmailEventListener(
    private val emailUseCase: com.dox.application.port.input.EmailUseCase,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener
    fun handle(event: WelcomeEmailRequestedEvent) {
        try {
            emailUseCase.sendWelcome(event.command)
        } catch (e: Exception) {
            log.error("Failed to send welcome email to {}: {}", event.command.recipient, e.message, e)
        }
    }
}
