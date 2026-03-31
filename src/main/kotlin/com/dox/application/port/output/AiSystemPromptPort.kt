package com.dox.application.port.output

import com.dox.domain.enum.Vertical

interface AiSystemPromptPort {
    fun build(vertical: Vertical): String

    fun buildPlanningPrompt(vertical: Vertical, sectionTitles: String, dataSummary: String): String?
}
