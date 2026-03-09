package com.dox.adapter.`in`.rest.dto.report

import com.dox.domain.enum.ReportStatus
import java.time.LocalDateTime
import java.util.UUID

data class ReportRequest(
    val status: ReportStatus? = null,
    val customerName: String? = null,
    val customerId: UUID? = null,
    val blocks: List<Map<String, Any?>> = emptyList()
)

data class ReportResponse(
    val id: UUID,
    val status: ReportStatus,
    val customerName: String?,
    val customerId: UUID?,
    val formResponseId: UUID?,
    val blocks: List<Map<String, Any?>>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class ReportVersionRequest(
    val description: String? = null,
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
