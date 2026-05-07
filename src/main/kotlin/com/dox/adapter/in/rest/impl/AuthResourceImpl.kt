package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.auth.AuthResponse
import com.dox.adapter.`in`.rest.dto.auth.LoginRequest
import com.dox.adapter.`in`.rest.dto.auth.RefreshRequest
import com.dox.adapter.`in`.rest.dto.auth.RegisterRequest
import com.dox.adapter.`in`.rest.dto.auth.SwitchTenantRequest
import com.dox.adapter.`in`.rest.dto.auth.VerifyEmailRequest
import com.dox.adapter.`in`.rest.dto.auth.VerifyEmailResponse
import com.dox.adapter.`in`.rest.resource.AuthResource
import com.dox.adapter.out.email.config.EmailProperties
import com.dox.application.port.input.AuthUseCase
import com.dox.application.port.input.LoginCommand
import com.dox.application.port.input.RegisterCommand
import com.dox.application.port.input.SwitchTenantCommand
import com.dox.domain.exception.InvalidTokenException
import com.dox.domain.exception.TokenExpiredException
import com.dox.shared.ContextHolder
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class AuthResourceImpl(
    private val authUseCase: AuthUseCase,
    private val emailProperties: EmailProperties,
) : AuthResource {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun register(request: RegisterRequest): ResponseEntity<AuthResponse> {
        val result =
            authUseCase.register(
                RegisterCommand(
                    email = request.email,
                    name = request.name,
                    password = request.password,
                    vertical = request.vertical,
                ),
            )
        return responseEntity(result.toResponse(), HttpStatus.CREATED)
    }

    override fun login(request: LoginRequest): ResponseEntity<AuthResponse> {
        val result =
            authUseCase.login(
                LoginCommand(email = request.email, password = request.password),
            )
        return responseEntity(result.toResponse())
    }

    override fun refresh(request: RefreshRequest): ResponseEntity<AuthResponse> {
        val result = authUseCase.refresh(request.refreshToken)
        return responseEntity(result.toResponse())
    }

    override fun logout(): ResponseEntity<Void> {
        val userId = ContextHolder.getUserIdOrThrow()
        authUseCase.logout(userId)
        return noContent()
    }

    override fun switchTenant(request: SwitchTenantRequest): ResponseEntity<AuthResponse> {
        val userId = ContextHolder.getUserIdOrThrow()
        val result =
            authUseCase.switchTenant(
                SwitchTenantCommand(userId = userId, tenantId = request.tenantId),
            )
        return responseEntity(result.toResponse())
    }

    override fun verifyEmail(request: VerifyEmailRequest): ResponseEntity<VerifyEmailResponse> {
        val result = authUseCase.verifyEmail(request.token)
        return responseEntity(
            VerifyEmailResponse(
                verified = result.verified,
                alreadyVerified = result.alreadyVerified,
                email = result.email,
            ),
        )
    }

    override fun verifyEmailRedirect(token: String): ResponseEntity<Void> {
        val location =
            try {
                val result = authUseCase.verifyEmail(token)
                val flag = if (result.alreadyVerified) "already" else "true"
                "${emailProperties.frontendUrl.trimEnd('/')}/login?confirmed=$flag"
            } catch (e: TokenExpiredException) {
                "${emailProperties.landingUrl.trimEnd('/')}/verify-email-error?reason=expired"
            } catch (e: InvalidTokenException) {
                "${emailProperties.landingUrl.trimEnd('/')}/verify-email-error?reason=invalid"
            } catch (e: Exception) {
                log.error("Falha inesperada em verify-email-redirect", e)
                "${emailProperties.landingUrl.trimEnd('/')}/verify-email-error?reason=generic"
            }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(location)).build()
    }

    private fun com.dox.application.port.input.AuthResult.toResponse() =
        AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            email = email,
            name = name,
            tenantId = tenantId,
            vertical = vertical,
            emailVerified = emailVerified,
            customerLabel = customerLabel,
        )
}
