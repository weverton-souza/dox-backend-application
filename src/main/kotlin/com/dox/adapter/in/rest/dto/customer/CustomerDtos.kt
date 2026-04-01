package com.dox.adapter.`in`.rest.dto.customer

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

data class CustomerRequest(
    @field:Size(max = 100, message = "Máximo de 100 campos permitidos")
    val data: Map<String, Any?>
)

data class CustomerResponse(
    val id: UUID,
    val data: Map<String, Any?>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class CustomerNoteRequest(
    @field:NotBlank(message = "Conteúdo é obrigatório")
    @field:Size(max = 10000, message = "Conteúdo deve ter no máximo 10000 caracteres")
    val content: String
)

data class CustomerNoteResponse(
    val id: UUID,
    val customerId: UUID,
    val content: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class CustomerEventRequest(
    @field:NotBlank(message = "Tipo é obrigatório")
    @field:Size(max = 100, message = "Tipo deve ter no máximo 100 caracteres")
    val type: String,
    @field:NotBlank(message = "Título é obrigatório")
    @field:Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    val title: String,
    @field:Size(max = 2000, message = "Descrição deve ter no máximo 2000 caracteres")
    val description: String? = null,
    @field:NotNull(message = "Data é obrigatória")
    val date: LocalDateTime
)

data class CustomerEventResponse(
    val id: UUID,
    val customerId: UUID,
    val type: String,
    val title: String,
    val description: String?,
    val date: LocalDateTime,
    val createdAt: LocalDateTime?
)

data class CustomerCalendarEventResponse(
    val id: UUID,
    val customerId: UUID,
    val customerName: String,
    val type: String,
    val title: String,
    val description: String?,
    val date: LocalDateTime,
    val createdAt: LocalDateTime?
)
