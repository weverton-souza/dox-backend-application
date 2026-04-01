package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.auth.AuthResponse
import com.dox.adapter.`in`.rest.dto.auth.LoginRequest
import com.dox.adapter.`in`.rest.dto.auth.RefreshRequest
import com.dox.adapter.`in`.rest.dto.auth.RegisterRequest
import com.dox.adapter.`in`.rest.dto.auth.SwitchTenantRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Autenticação", description = "Registro, login, refresh e switch de tenant")
@RequestMapping("/auth")
interface AuthResource : BaseResource {
    @Operation(summary = "Registrar novo usuário", security = [])
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequest,
    ): ResponseEntity<AuthResponse>

    @Operation(summary = "Login com email e senha", security = [])
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): ResponseEntity<AuthResponse>

    @Operation(summary = "Renovar tokens", security = [])
    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody request: RefreshRequest,
    ): ResponseEntity<AuthResponse>

    @Operation(summary = "Logout (revoga refresh tokens)")
    @PostMapping("/logout")
    fun logout(): ResponseEntity<Void>

    @Operation(summary = "Trocar de workspace/tenant")
    @PostMapping("/switch-tenant")
    fun switchTenant(
        @Valid @RequestBody request: SwitchTenantRequest,
    ): ResponseEntity<AuthResponse>
}
