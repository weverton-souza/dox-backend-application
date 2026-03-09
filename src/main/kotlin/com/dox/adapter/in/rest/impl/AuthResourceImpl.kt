package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.auth.AuthResponse
import com.dox.adapter.`in`.rest.dto.auth.LoginRequest
import com.dox.adapter.`in`.rest.dto.auth.RefreshRequest
import com.dox.adapter.`in`.rest.dto.auth.RegisterRequest
import com.dox.adapter.`in`.rest.dto.auth.SwitchTenantRequest
import com.dox.adapter.`in`.rest.resource.AuthResource
import com.dox.application.port.input.AuthUseCase
import com.dox.application.port.input.LoginCommand
import com.dox.application.port.input.RegisterCommand
import com.dox.application.port.input.SwitchTenantCommand
import com.dox.shared.ContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthResourceImpl(
    private val authUseCase: AuthUseCase
) : AuthResource {

    override fun register(request: RegisterRequest): ResponseEntity<AuthResponse> {
        val result = authUseCase.register(
            RegisterCommand(
                email = request.email,
                name = request.name,
                password = request.password,
                vertical = request.vertical
            )
        )
        return responseEntity(result.toResponse(), HttpStatus.CREATED)
    }

    override fun login(request: LoginRequest): ResponseEntity<AuthResponse> {
        val result = authUseCase.login(
            LoginCommand(email = request.email, password = request.password)
        )
        return responseEntity(result.toResponse())
    }

    override fun refresh(request: RefreshRequest): ResponseEntity<AuthResponse> {
        val result = authUseCase.refresh(request.refreshToken)
        return responseEntity(result.toResponse())
    }

    override fun logout(): ResponseEntity<Void> {
        val userId = ContextHolder.context.userId
            ?: throw IllegalStateException("Usuário não autenticado")
        authUseCase.logout(userId)
        return noContent()
    }

    override fun switchTenant(request: SwitchTenantRequest): ResponseEntity<AuthResponse> {
        val userId = ContextHolder.context.userId
            ?: throw IllegalStateException("Usuário não autenticado")
        val result = authUseCase.switchTenant(
            SwitchTenantCommand(userId = userId, tenantId = request.tenantId)
        )
        return responseEntity(result.toResponse())
    }

    private fun com.dox.application.port.input.AuthResult.toResponse() = AuthResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
        userId = userId,
        email = email,
        name = name,
        tenantId = tenantId,
        vertical = vertical
    )
}
