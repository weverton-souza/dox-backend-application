package com.dox.adapter.`in`.rest.dto.waitlist

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class WaitlistRequest(
    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    val name: String,
    @field:NotBlank(message = "Email é obrigatório")
    @field:Email(message = "Email inválido")
    val email: String,
    @field:NotBlank(message = "Profissão é obrigatória")
    @field:Size(max = 100, message = "Profissão deve ter no máximo 100 caracteres")
    val profession: String,
    val city: String? = null,
)

data class WaitlistResponse(
    val message: String,
)
