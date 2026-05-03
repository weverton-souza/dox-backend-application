package com.dox.domain.report

import java.time.LocalDateTime
import java.util.UUID

data class PublishedReport(
    val id: UUID? = null,
    val reportId: UUID,
    val tenantId: UUID,
    val verificationCode: String,
    val contentHash: String,
    val finalizedAt: LocalDateTime,
    val professionalName: String? = null,
    val professionalCouncil: String? = null,
    val customerInitials: String? = null,
    val publishedAt: LocalDateTime? = null,
)
