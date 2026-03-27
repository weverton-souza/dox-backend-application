package com.dox.adapter.out.ai.prompt

import com.dox.application.port.input.GenerationPlan
import com.dox.application.port.input.SectionPlan
import com.dox.application.port.output.AiPlanningParserPort
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PlanningResponseParser : AiPlanningParserPort {

    private val log = LoggerFactory.getLogger(javaClass)
    private val objectMapper = jacksonObjectMapper()

    override fun parse(raw: String, sectionTitles: List<String>): GenerationPlan {
        val cleaned = cleanMarkdown(raw)
        return try {
            val parsed = objectMapper.readValue<GenerationPlan>(cleaned)
            validatePlan(parsed, sectionTitles)
        } catch (e: Exception) {
            log.warn("Failed to parse planning response, using default plan: {}", e.message)
            log.debug("Raw planning response: {}", raw.take(500))
            buildDefaultPlan(sectionTitles)
        }
    }

    private fun cleanMarkdown(raw: String): String {
        var text = raw.trim()
        if (text.startsWith("```")) {
            text = text.replaceFirst(Regex("^```(?:json)?\\s*"), "")
            text = text.replaceFirst(Regex("\\s*```$"), "")
            text = text.trim()
        }
        return text
    }

    private fun validatePlan(plan: GenerationPlan, sectionTitles: List<String>): GenerationPlan {
        val planTitles = plan.sections.map { it.title }.toSet()
        val missingSections = sectionTitles.filter { it !in planTitles }

        if (missingSections.isEmpty()) return plan

        val additionalSections = missingSections.map { title ->
            SectionPlan(title = title, status = "full")
        }
        return plan.copy(sections = plan.sections + additionalSections)
    }

    private fun buildDefaultPlan(sectionTitles: List<String>): GenerationPlan {
        return GenerationPlan(
            verticalContext = "",
            sections = sectionTitles.map { SectionPlan(title = it, status = "full") }
        )
    }
}
