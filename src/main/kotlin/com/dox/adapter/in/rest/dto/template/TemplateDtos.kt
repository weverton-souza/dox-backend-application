package com.dox.adapter.`in`.rest.dto.template

import java.time.LocalDateTime
import java.util.UUID

data class ReportTemplateRequest(
    val name: String,
    val description: String? = null,
    val blocks: List<Map<String, Any?>> = emptyList()
)

data class ReportTemplateResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val blocks: List<Map<String, Any?>>,
    val isDefault: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class ScoreTableTemplateRequest(
    val name: String,
    val description: String? = null,
    val instrumentName: String? = null,
    val category: String? = null,
    val columns: List<Map<String, Any?>> = emptyList(),
    val rows: List<Map<String, Any?>> = emptyList()
)

data class ScoreTableTemplateResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val instrumentName: String?,
    val category: String?,
    val columns: List<Map<String, Any?>>,
    val rows: List<Map<String, Any?>>,
    val isDefault: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class ChartTemplateRequest(
    val name: String,
    val description: String? = null,
    val instrumentName: String? = null,
    val category: String? = null,
    val data: Map<String, Any?> = emptyMap()
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
    val updatedAt: LocalDateTime?
)
