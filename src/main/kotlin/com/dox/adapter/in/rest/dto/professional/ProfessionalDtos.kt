package com.dox.adapter.`in`.rest.dto.professional

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import java.util.UUID

data class ProfessionalRequest(
    @field:Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    val name: String? = null,
    @field:Size(max = 50, message = "CRP deve ter no máximo 50 caracteres")
    val crp: String? = null,
    @field:Size(max = 255, message = "Especialização deve ter no máximo 255 caracteres")
    val specialization: String? = null,
    @field:Size(max = 30, message = "Telefone deve ter no máximo 30 caracteres")
    val phone: String? = null,
    @field:Size(max = 100, message = "Instagram deve ter no máximo 100 caracteres")
    val instagram: String? = null,
    @field:Email(message = "Email inválido")
    val email: String? = null,
    val logo: String? = null,
    val contactItems: List<Map<String, Any?>> = emptyList(),
)

data class ProfessionalResponse(
    val id: UUID?,
    val name: String,
    val crp: String?,
    val specialization: String,
    val phone: String?,
    val instagram: String?,
    val email: String?,
    val logo: String?,
    val contactItems: List<Map<String, Any?>>,
)
