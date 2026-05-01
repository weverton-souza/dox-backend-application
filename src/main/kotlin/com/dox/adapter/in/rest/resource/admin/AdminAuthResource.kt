package com.dox.adapter.`in`.rest.resource.admin

import com.dox.adapter.`in`.rest.dto.admin.AdminAuthResponse
import com.dox.adapter.`in`.rest.dto.admin.AdminLoginRequest
import com.dox.adapter.`in`.rest.dto.admin.AdminMeResponse
import com.dox.adapter.`in`.rest.resource.BaseResource
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Admin · Autenticação", description = "Login e sessão de administradores do backoffice")
@RequestMapping("/admin/auth")
interface AdminAuthResource : BaseResource {
    @Operation(summary = "Login do administrador", security = [])
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: AdminLoginRequest,
    ): ResponseEntity<AdminAuthResponse>

    @Operation(summary = "Logout do administrador (no-op no backend, cookie limpo no cliente)")
    @PostMapping("/logout")
    fun logout(): ResponseEntity<Void>

    @Operation(summary = "Dados do administrador autenticado")
    @GetMapping("/me")
    fun me(): ResponseEntity<AdminMeResponse>
}
