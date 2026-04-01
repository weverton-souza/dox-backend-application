package com.dox.adapter.`in`.rest.dto.template

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

data class ReportTemplateRequest(
    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    val name: String,
    @field:Size(max = 2000, message = "Descrição deve ter no máximo 2000 caracteres")
    val description: String? = null,
    @field:Size(max = 200, message = "Máximo de 200 blocos permitidos")
    val blocks: List<Map<String, Any?>> = emptyList(),
    val isLocked: Boolean = false,
)

data class ReportTemplateResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val blocks: List<Map<String, Any?>>,
    val isDefault: Boolean,
    val isLocked: Boolean,
    val isMaster: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)

data class ScoreTableTemplateRequest(
    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    val name: String,
    @field:Size(max = 2000, message = "Descrição deve ter no máximo 2000 caracteres")
    val description: String? = null,
    @field:Size(max = 255, message = "Nome do instrumento deve ter no máximo 255 caracteres")
    val instrumentName: String? = null,
    @field:Size(max = 100, message = "Categoria deve ter no máximo 100 caracteres")
    val category: String? = null,
    @field:Size(max = 100, message = "Máximo de 100 colunas permitidas")
    val columns: List<Map<String, Any?>> = emptyList(),
    @field:Size(max = 500, message = "Máximo de 500 linhas permitidas")
    val rows: List<Map<String, Any?>> = emptyList(),
)

data class ScoreTableTemplateResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val instrumentName: String?,
    val category: String?,
    val columns: List<Map<String, Any?>>,
    val rows: List<Map<String, Any?>>,
    val footnote: List<Map<String, Any?>>?,
    val isDefault: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)

data class ChartTemplateRequest(
    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    val name: String,
    @field:Size(max = 2000, message = "Descrição deve ter no máximo 2000 caracteres")
    val description: String? = null,
    @field:Size(max = 255, message = "Nome do instrumento deve ter no máximo 255 caracteres")
    val instrumentName: String? = null,
    @field:Size(max = 100, message = "Categoria deve ter no máximo 100 caracteres")
    val category: String? = null,
    val data: Map<String, Any?> = emptyMap(),
)

data class ChartTemplateResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val instrumentName: String?,
    val category: String?,
    val data: Map<String, Any?>,
    val isDefault: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)
