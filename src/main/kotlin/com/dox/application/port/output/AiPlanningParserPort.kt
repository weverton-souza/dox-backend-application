package com.dox.application.port.output

import com.dox.application.port.input.GenerationPlan

interface AiPlanningParserPort {
    fun parse(
        raw: String,
        sectionTitles: List<String>,
    ): GenerationPlan
}
