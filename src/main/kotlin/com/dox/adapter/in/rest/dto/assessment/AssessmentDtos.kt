package com.dox.adapter.`in`.rest.dto.assessment

import com.dox.domain.enum.AssessmentEntryType
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class AssessmentScoreDto(
    @field:NotBlank(message = "Índice é obrigatório")
    @field:Size(max = 50, message = "Índice deve ter no máximo 50 caracteres")
    val index: String,
    @field:NotBlank(message = "Nome do escore é obrigatório")
    @field:Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    val label: String,
    @field:NotBlank(message = "Valor é obrigatório")
    @field:Size(max = 100, message = "Valor deve ter no máximo 100 caracteres")
    val value: String,
    @field:Size(max = 100, message = "Classificação deve ter no máximo 100 caracteres")
    val classification: String? = null,
)

data class AssessmentEntryRequest(
    val id: UUID? = null,
    @field:NotBlank(message = "Nome do instrumento é obrigatório")
    @field:Size(max = 255, message = "Nome do instrumento deve ter no máximo 255 caracteres")
    val instrumentName: String,
    val entryType: AssessmentEntryType,
    val orderIndex: Int = 0,
    @field:Size(max = 50, message = "Máximo de 50 escores por registro")
    val scores: List<@Valid AssessmentScoreDto> = emptyList(),
    val block: Map<String, Any?>? = null,
    @field:Size(max = 5000, message = "Observações devem ter no máximo 5000 caracteres")
    val observations: String? = null,
    val attachmentFileId: UUID? = null,
)

data class AssessmentRequest(
    @field:NotBlank(message = "Título é obrigatório")
    @field:Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    val title: String,
    @field:Size(max = 100, message = "Categoria deve ter no máximo 100 caracteres")
    val category: String? = null,
    @field:PastOrPresent(message = "Data da avaliação não pode ser futura")
    val appliedAt: LocalDate,
    val appointmentId: UUID? = null,
    val parentAssessmentId: UUID? = null,
    @field:Size(max = 5000, message = "Observações devem ter no máximo 5000 caracteres")
    val notes: String? = null,
    @field:Size(min = 1, max = 50, message = "Avaliação precisa ter entre 1 e 50 registros")
    val entries: List<@Valid AssessmentEntryRequest> = emptyList(),
)

data class AssessmentEntryResponse(
    val id: UUID,
    val instrumentName: String,
    val entryType: AssessmentEntryType,
    val orderIndex: Int,
    val scores: List<AssessmentScoreDto>,
    val block: Map<String, Any?>?,
    val observations: String?,
    val attachmentFileId: UUID?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)

data class AssessmentResponse(
    val id: UUID,
    val customerId: UUID,
    val appointmentId: UUID?,
    val applierId: UUID,
    val title: String,
    val category: String?,
    val appliedAt: LocalDate,
    val notes: String?,
    val parentAssessmentId: UUID?,
    val professionalDeclarationAcceptedAt: LocalDateTime,
    val professionalDeclarationRevision: Int,
    val entries: List<AssessmentEntryResponse>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)

data class RelatedTemplateResponse(
    val id: UUID,
    val name: String,
    val type: String,
    val instrumentName: String?,
    val category: String?,
)
