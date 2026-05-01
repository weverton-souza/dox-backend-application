package com.dox.adapter.`in`.rest.impl.admin

import com.dox.adapter.`in`.rest.dto.admin.AdminAuthResponse
import com.dox.adapter.`in`.rest.dto.admin.AdminLoginRequest
import com.dox.adapter.`in`.rest.dto.admin.AdminMeResponse
import com.dox.adapter.`in`.rest.resource.admin.AdminAuthResource
import com.dox.application.port.input.AdminAuthUseCase
import com.dox.application.port.input.AdminLoginCommand
import com.dox.shared.ContextHolder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminAuthResourceImpl(
    private val adminAuthUseCase: AdminAuthUseCase,
) : AdminAuthResource {
    override fun login(request: AdminLoginRequest): ResponseEntity<AdminAuthResponse> {
        val result =
            adminAuthUseCase.login(
                AdminLoginCommand(email = request.email, password = request.password),
            )
        return responseEntity(
            AdminAuthResponse(
                accessToken = result.accessToken,
                adminId = result.adminId,
                email = result.email,
                name = result.name,
                role = result.role,
            ),
        )
    }

    override fun logout(): ResponseEntity<Void> = noContent()

    override fun me(): ResponseEntity<AdminMeResponse> {
        val adminId = ContextHolder.getUserIdOrThrow()
        val admin = adminAuthUseCase.me(adminId)
        return responseEntity(
            AdminMeResponse(
                id = admin.id,
                email = admin.email,
                name = admin.name,
                role = admin.role,
                lastLoginAt = admin.lastLoginAt,
            ),
        )
    }
}
