package com.dox.adapter.out.ai

import com.dox.application.port.output.AiGenerationPort
import com.dox.domain.model.AiGenerationResult
import org.springframework.stereotype.Component

// Intentional placeholder: ensures AiGenerationPort has a bean even when no AI provider is configured
@Component("fallbackAiAdapter")
class FallbackAiAdapter : AiGenerationPort {

    override fun generateSection(systemPrompt: String, userPrompt: String, model: String): AiGenerationResult {
        throw UnsupportedOperationException("Fallback AI provider não configurado")
    }
}
