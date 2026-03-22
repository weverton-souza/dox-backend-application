package com.dox.adapter.out.ai

import com.dox.application.port.output.AiGenerationPort
import com.dox.domain.model.AiGenerationResult
import org.slf4j.LoggerFactory
import org.springframework.ai.anthropic.AnthropicChatModel
import org.springframework.ai.anthropic.AnthropicChatOptions
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Component
import java.util.UUID

@Component("anthropicAiAdapter")
class AnthropicAiAdapter(
    private val chatModel: AnthropicChatModel
) : AiGenerationPort {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun generateSection(systemPrompt: String, userPrompt: String, model: String): AiGenerationResult {
        log.info("=== AI GENERATION REQUEST === Model: {}, systemPromptChars={}, userPromptChars={}", model, systemPrompt.length, userPrompt.length)
        log.debug("System prompt preview: {}", systemPrompt.take(200))
        log.debug("User prompt preview: {}", userPrompt.take(300))

        val options = AnthropicChatOptions.builder()
            .model(model)
            .maxTokens(MAX_TOKENS)
            .temperature(TEMPERATURE)
            .build()

        val prompt = Prompt(
            listOf(SystemMessage(systemPrompt), UserMessage(userPrompt)),
            options
        )

        val startTime = System.currentTimeMillis()
        try {
            val response = chatModel.call(prompt)
            val durationMs = (System.currentTimeMillis() - startTime).toInt()

        val text = response.result?.output?.text ?: ""
        log.debug("Raw AI response (first 500 chars): {}", text.take(500))
        val usage = response.metadata?.usage

        val inputTokens = usage?.promptTokens ?: 0
        val outputTokens = usage?.completionTokens ?: 0

        log.info(
            "Anthropic generation completed: model={}, inputTokens={}, outputTokens={}, durationMs={}",
            model, inputTokens, outputTokens, durationMs
        )

        return AiGenerationResult(
            generationId = UUID.randomUUID(),
            text = extractText(text),
            model = model,
            inputTokens = inputTokens,
            outputTokens = outputTokens,
            // TODO: extract actual cache token counts when prompt caching is enabled
            cacheReadTokens = 0,
            cacheWriteTokens = 0,
            cached = false,
            durationMs = durationMs
        )
        } catch (e: Exception) {
            val durationMs = (System.currentTimeMillis() - startTime).toInt()
            log.error("=== AI GENERATION FAILED ({}ms) ===", durationMs)
            log.error("Error type: {}", e.javaClass.simpleName)
            log.error("Error message: {}", e.message)
            if (e.cause != null) {
                log.error("Caused by: {}", e.cause?.message)
            }
            throw e
        }
    }

    private val objectMapper = jacksonObjectMapper()

    private fun extractText(raw: String): String {
        var trimmed = raw.trim()

        if (trimmed.startsWith("```")) {
            trimmed = trimmed
                .removePrefix("```json").removePrefix("```")
                .removeSuffix("```")
                .trim()
        }

        if (trimmed.startsWith("{")) {
            try {
                val map = objectMapper.readValue<Map<String, Any>>(trimmed)
                return map["text"]?.toString() ?: trimmed
            } catch (_: Exception) { }
        }

        return trimmed
    }

    companion object {
        private const val MAX_TOKENS = 8000
        private const val TEMPERATURE = 0.3
    }
}
