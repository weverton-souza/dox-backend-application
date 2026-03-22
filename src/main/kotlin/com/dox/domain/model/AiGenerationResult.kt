package com.dox.domain.model

import java.util.UUID

data class AiGenerationResult(
    val generationId: UUID = UUID.randomUUID(),
    val text: String,
    val model: String,
    val inputTokens: Int = 0,
    val outputTokens: Int = 0,
    val cacheReadTokens: Int = 0,
    val cacheWriteTokens: Int = 0,
    val cached: Boolean = false,
    val durationMs: Int = 0
)
