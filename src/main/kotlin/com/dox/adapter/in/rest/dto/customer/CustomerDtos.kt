package com.dox.adapter.`in`.rest.dto.customer

import com.dox.domain.enum.PatientContactRelationType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

data class CustomerRequest(
    @field:Size(max = 100, message = "Máximo de 100 campos permitidos")
    val data: Map<String, Any?>,
)

data class CustomerResponse(
    val id: UUID,
    val data: Map<String, Any?>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)

data class CustomerNoteRequest(
    @field:NotBlank(message = "Conteúdo é obrigatório")
    @field:Size(max = 10000, message = "Conteúdo deve ter no máximo 10000 caracteres")
    val content: String,
)

data class CustomerNoteResponse(
    val id: UUID,
    val customerId: UUID,
    val content: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
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
    val date: LocalDateTime,
)

data class CustomerEventResponse(
    val id: UUID,
    val customerId: UUID,
    val type: String,
    val title: String,
    val description: String?,
    val date: LocalDateTime,
    val createdAt: LocalDateTime?,
)

data class CustomerCalendarEventResponse(
    val id: UUID,
    val customerId: UUID,
    val customerName: String,
    val type: String,
    val title: String,
    val description: String?,
    val date: LocalDateTime,
    val createdAt: LocalDateTime?,
)

data class PatientContactRequest(
    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    val name: String,
    @field:NotNull(message = "Tipo de relação é obrigatório")
    val relationType: PatientContactRelationType,
    @field:Email(message = "Email inválido")
    @field:Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    val email: String? = null,
    @field:Size(max = 50, message = "Telefone deve ter no máximo 50 caracteres")
    val phone: String? = null,
    @field:Size(max = 2000, message = "Observações devem ter no máximo 2000 caracteres")
    val notes: String? = null,
    val canReceiveForms: Boolean = true,
)

data class PatientContactResponse(
    val id: UUID,
    val customerId: UUID,
    val name: String,
    val relationType: PatientContactRelationType,
    val email: String?,
    val phone: String?,
    val notes: String?,
    val canReceiveForms: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)
