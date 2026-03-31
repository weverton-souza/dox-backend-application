package com.dox.adapter.`in`.rest.dto.reference

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

data class ReferenceEntryRequest(
    @field:NotBlank(message = "Texto da referência é obrigatório")
    val text: String,

    @field:Size(max = 200, message = "Instrumento deve ter no máximo 200 caracteres")
    val instrument: String? = null,

    @field:Size(max = 500, message = "Autores deve ter no máximo 500 caracteres")
    val authors: String? = null,

    val year: Int? = null
)

data class ReferenceEntryResponse(
    val id: UUID,
    val text: String,
    val instrument: String?,
    val authors: String?,
    val year: Int?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
