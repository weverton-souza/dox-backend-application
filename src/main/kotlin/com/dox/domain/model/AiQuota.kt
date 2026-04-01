package com.dox.domain.model

import com.dox.domain.enum.AiTier
import java.time.LocalDateTime
import java.util.UUID

data class AiQuota(
    val id: UUID = UUID.randomUUID(),
    val aiTier: AiTier = AiTier.NONE,
    val model: String = "claude-sonnet-4-6",
    val monthlyLimit: Int = 0,
    val overagePriceCents: Int = 0,
    val enabled: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
