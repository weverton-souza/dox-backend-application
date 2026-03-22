package com.dox.adapter.out.ai.prompt

import org.springframework.stereotype.Component

@Component
class PromptSanitizer {

    private val instructionPatterns = listOf(
        Regex("(?i)(ignore|disregard|forget|override|bypass)\\s+(all\\s+)?(previous|above|prior|system|instructions|rules|prompts)", RegexOption.IGNORE_CASE),
        Regex("(?i)(you\\s+are|act\\s+as|pretend|roleplay|simulate|behave)\\s+(now\\s+)?", RegexOption.IGNORE_CASE),
        Regex("(?i)(system\\s*prompt|system\\s*message|hidden\\s*instruction)", RegexOption.IGNORE_CASE),
        Regex("(?i)(do\\s+not\\s+follow|stop\\s+following|new\\s+instructions?)", RegexOption.IGNORE_CASE),
        Regex("(?i)\\{\\{.*?\\}\\}"),
        Regex("(?i)<\\s*(system|assistant|user)\\s*>")
    )

    fun sanitize(input: String): String {
        if (input.isBlank()) return input

        var sanitized = input
            .replace(Regex("<[^>]*>"), "")
            .replace("\u0000", "")

        instructionPatterns.forEach { pattern ->
            sanitized = sanitized.replace(pattern, "[REDACTED]")
        }

        return sanitized.take(MAX_FIELD_LENGTH)
    }

    companion object {
        private const val MAX_FIELD_LENGTH = 5000
    }
}
