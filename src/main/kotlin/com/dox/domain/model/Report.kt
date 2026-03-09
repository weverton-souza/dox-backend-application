package com.dox.domain.model

import com.dox.domain.enum.ReportStatus
import java.time.LocalDateTime
import java.util.UUID

data class Report(
    val id: UUID = UUID.randomUUID(),
    val status: ReportStatus = ReportStatus.RASCUNHO,
    val customerName: String? = null,
    val customerId: UUID? = null,
    val formResponseId: UUID? = null,
    val blocks: List<Map<String, Any?>> = emptyList(),
    val deleted: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

data class ReportVersion(
    val id: UUID = UUID.randomUUID(),
    val reportId: UUID,
    val status: ReportStatus,
    val description: String? = null,
    val customerName: String? = null,
    val blocks: List<Map<String, Any?>> = emptyList(),
    val type: String = "manual",
    val createdAt: LocalDateTime? = null
)
