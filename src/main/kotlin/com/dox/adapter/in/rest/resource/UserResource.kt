package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.auth.UpdateUserRequest
import com.dox.adapter.`in`.rest.dto.auth.UserResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Usuário", description = "Dados do usuário autenticado")
@RequestMapping("/users")
interface UserResource : BaseResource {
    @Operation(summary = "Obter dados do usuário autenticado")
    @GetMapping("/me")
    fun getMe(): ResponseEntity<UserResponse>

    @Operation(summary = "Atualizar dados do usuário autenticado")
    @PutMapping("/me")
    fun updateMe(
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<UserResponse>
}
