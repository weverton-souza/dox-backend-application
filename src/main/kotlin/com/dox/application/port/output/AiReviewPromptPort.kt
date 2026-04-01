package com.dox.application.port.output

import com.dox.domain.enum.Vertical
import com.dox.domain.model.FormResponse

interface AiReviewPromptPort {
    fun buildSystemPrompt(vertical: Vertical): String

    fun buildUserPrompt(
        text: String,
        action: String,
        sectionType: String?,
        instruction: String?,
        formResponses: List<FormResponse>?,
    ): String
}
