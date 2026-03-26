package com.dox.adapter.`in`.rest.dto.report

import com.dox.domain.enum.ReportStatus
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

data class ReportRequest(
    val status: ReportStatus? = null,

    @field:Size(max = 255, message = "Nome do cliente deve ter no máximo 255 caracteres")
    val customerName: String? = null,

    val customerId: UUID? = null,
    val formResponseId: UUID? = null,
    val templateId: UUID? = null,
    val isStructureLocked: Boolean = false,
    val blocks: List<Map<String, Any?>> = emptyList()
)

data class ReportResponse(
    val id: UUID,
    val status: ReportStatus,
    val customerName: String?,
    val customerId: UUID?,
    val formResponseId: UUID?,
    val templateId: UUID?,
    val isStructureLocked: Boolean,
    val blocks: List<Map<String, Any?>>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class ReportVersionRequest(
    @field:Size(max = 2000, message = "Descrição deve ter no máximo 2000 caracteres")
    val description: String? = null,

    @field:Size(max = 50, message = "Tipo deve ter no máximo 50 caracteres")
    val type: String = "manual"
)

data class ReportVersionResponse(
    val id: UUID,
    val reportId: UUID,
    val status: ReportStatus,
    val description: String?,
    val customerName: String?,
    val blocks: List<Map<String, Any?>>,
    val type: String,
    val createdAt: LocalDateTime?
)
