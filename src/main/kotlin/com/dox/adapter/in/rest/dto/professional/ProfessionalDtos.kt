package com.dox.adapter.`in`.rest.dto.professional

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.util.UUID

data class ProfessionalRequest(
    @field:Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    val name: String? = null,
    @field:Size(max = 255, message = "Nome social deve ter no máximo 255 caracteres")
    val socialName: String? = null,
    @field:Size(max = 20, message = "Gênero deve ter no máximo 20 caracteres")
    val gender: String? = null,
    @field:Size(max = 50, message = "CRP deve ter no máximo 50 caracteres")
    val crp: String? = null,
    @field:Size(max = 20, message = "Tipo de conselho deve ter no máximo 20 caracteres")
    val councilType: String? = null,
    @field:Size(max = 50, message = "Número do conselho deve ter no máximo 50 caracteres")
    val councilNumber: String? = null,
    @field:Pattern(regexp = "^[A-Z]{2}$|^$", message = "UF deve ter 2 letras maiúsculas")
    val councilState: String? = null,
    @field:Size(max = 255, message = "Especialização deve ter no máximo 255 caracteres")
    val specialization: String? = null,
    @field:Size(max = 500, message = "Bio deve ter no máximo 500 caracteres")
    val bio: String? = null,
    @field:Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    val addressCity: String? = null,
    @field:Pattern(regexp = "^[A-Z]{2}$|^$", message = "UF deve ter 2 letras maiúsculas")
    val addressState: String? = null,
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
    val socialName: String?,
    val gender: String?,
    val crp: String?,
    val councilType: String?,
    val councilNumber: String?,
    val councilState: String?,
    val specialization: String,
    val bio: String?,
    val addressCity: String?,
    val addressState: String?,
    val phone: String?,
    val instagram: String?,
    val email: String?,
    val logo: String?,
    val contactItems: List<Map<String, Any?>>,
)
