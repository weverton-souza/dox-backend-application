package com.dox.adapter.`in`.rest.dto.contentlibrary

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

data class ContentLibraryRequest(
    @field:NotBlank(message = "Título é obrigatório")
    @field:Size(max = 300, message = "Título deve ter no máximo 300 caracteres")
    val title: String,
    @field:Size(max = 100, message = "Máximo de 100 itens de conteúdo permitidos")
    val content: List<Map<String, Any?>>,
    @field:NotBlank(message = "Tipo é obrigatório")
    val type: String = "reference",
    val category: String = "general",
    @field:Size(max = 200, message = "Instrumento deve ter no máximo 200 caracteres")
    val instrument: String? = null,
    @field:Size(max = 500, message = "Autores deve ter no máximo 500 caracteres")
    val authors: String? = null,
    val year: Int? = null,
    @field:Size(max = 500, message = "Tags deve ter no máximo 500 caracteres")
    val tags: String? = null
)

data class ContentLibraryResponse(
    val id: UUID,
    val title: String,
    val content: List<Map<String, Any?>>,
    val type: String,
    val category: String,
    val instrument: String?,
    val authors: String?,
    val year: Int?,
    val tags: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
