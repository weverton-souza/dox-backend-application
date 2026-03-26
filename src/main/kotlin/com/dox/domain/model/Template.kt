package com.dox.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class ReportTemplate(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String? = null,
    val blocks: List<Map<String, Any?>> = emptyList(),
    val isDefault: Boolean = false,
    val isLocked: Boolean = false,
    val isMaster: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

data class ScoreTableTemplate(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String? = null,
    val instrumentName: String? = null,
    val category: String? = null,
    val columns: List<Map<String, Any?>> = emptyList(),
    val rows: List<Map<String, Any?>> = emptyList(),
    val isDefault: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

data class ChartTemplate(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String? = null,
    val instrumentName: String? = null,
    val category: String? = null,
    val data: Map<String, Any?> = emptyMap(),
    val isDefault: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
