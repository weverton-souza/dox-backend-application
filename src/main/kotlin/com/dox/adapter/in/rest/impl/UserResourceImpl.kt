package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.auth.UpdateUserRequest
import com.dox.adapter.`in`.rest.dto.auth.UserResponse
import com.dox.adapter.`in`.rest.resource.UserResource
import com.dox.application.port.input.UpdateUserCommand
import com.dox.application.port.input.UserInfo
import com.dox.application.port.input.UserUseCase
import com.dox.shared.ContextHolder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserResourceImpl(
    private val userUseCase: UserUseCase
) : UserResource {

    override fun getMe(): ResponseEntity<UserResponse> {
        val userId = ContextHolder.context.userId
            ?: throw IllegalStateException("Usuário não autenticado")
        return responseEntity(userUseCase.getMe(userId).toResponse())
    }

    override fun updateMe(request: UpdateUserRequest): ResponseEntity<UserResponse> {
        val userId = ContextHolder.context.userId
            ?: throw IllegalStateException("Usuário não autenticado")
        val result = userUseCase.updateMe(
            UpdateUserCommand(userId = userId, name = request.name)
        )
        return responseEntity(result.toResponse())
    }

    private fun UserInfo.toResponse() = UserResponse(
        id = id,
        email = email,
        name = name,
        personalTenantId = personalTenantId
    )
}
