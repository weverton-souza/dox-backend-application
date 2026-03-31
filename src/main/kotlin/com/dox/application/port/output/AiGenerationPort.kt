package com.dox.application.port.output

import com.dox.domain.model.AiGenerationResult

interface AiGenerationPort {
    fun generateSection(systemPrompt: String, userPrompt: String, model: String, maxTokens: Int? = null): AiGenerationResult
}
