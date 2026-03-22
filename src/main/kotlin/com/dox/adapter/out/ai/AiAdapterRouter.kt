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
    @Qualifier("anthropicAiAdapter") private val anthropicAdapter: AiGenerationPort,
    @Qualifier("fallbackAiAdapter") private val fallbackAdapter: AiGenerationPort
) : AiGenerationPort {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun generateSection(systemPrompt: String, userPrompt: String, model: String): AiGenerationResult {
        return try {
            log.info("Attempting generation with Anthropic provider")
            anthropicAdapter.generateSection(systemPrompt, userPrompt, model)
        } catch (e: Exception) {
            if (isApiError(e)) {
                log.warn("Anthropic API error, attempting fallback: {}", e.message)
                try {
                    fallbackAdapter.generateSection(systemPrompt, userPrompt, model)
                } catch (fallbackEx: Exception) {
                    log.error("Fallback provider also failed: {}", fallbackEx.message)
                    throw e
                }
            } else {
                throw e
            }
        }
    }

    private fun isApiError(e: Exception): Boolean =
        e !is java.util.concurrent.TimeoutException &&
                e.message?.contains("timeout", ignoreCase = true) != true
}
