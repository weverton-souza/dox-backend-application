package com.dox.adapter.out.ai

import com.dox.application.port.output.AiGenerationPort
import com.dox.domain.model.AiGenerationResult
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component
class AiAdapterRouter(
    @Qualifier("anthropicAiAdapter") private val anthropicAdapter: AiGenerationPort
) : AiGenerationPort {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun generateSection(systemPrompt: String, userPrompt: String, model: String): AiGenerationResult {
        return try {
            log.info("Attempting generation with Anthropic provider")
            anthropicAdapter.generateSection(systemPrompt, userPrompt, model)
        } catch (e: Exception) {
            log.error("Anthropic generation failed: {}", e.message)
            throw e
        }
    }
}
