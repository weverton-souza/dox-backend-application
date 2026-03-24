package com.dox.adapter.out.ai.prompt

import com.dox.application.port.input.ComputedChartData
import com.dox.application.port.input.ComputedTableData
import com.dox.application.port.input.PreviousSectionContext
import com.dox.application.port.input.QuantitativeDataPayload
import com.dox.domain.model.Customer
import com.dox.domain.model.FormResponse
import com.dox.domain.model.ProfessionalSettings
import com.dox.domain.model.ReportTemplate
import com.dox.application.port.output.AiInstructionPort
import com.dox.domain.enum.Vertical
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SectionPromptBuilder(
    private val promptSanitizer: PromptSanitizer,
    private val aiInstructionPort: AiInstructionPort
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private val SAFE_CUSTOMER_FIELDS = setOf(
            "name", "age", "education", "profession",
            "chiefComplaint", "diagnosis", "medications", "referralDoctor",
            "guardianName", "guardianRelationship"
        )
    }

    fun buildContext(
        customer: Customer?,
        formResponse: FormResponse?,
        template: ReportTemplate?,
        professional: ProfessionalSettings?,
        quantitativeData: QuantitativeDataPayload? = null
    ): String = buildContext(customer, formResponse?.let { listOf(it) }, template, professional, quantitativeData)

    fun buildContext(
        customer: Customer?,
        formResponses: List<FormResponse>?,
        template: ReportTemplate?,
        professional: ProfessionalSettings?,
        quantitativeData: QuantitativeDataPayload? = null
    ): String {
        val parts = mutableListOf<String>()

        template?.let { parts.add(buildTemplateSection(it)) }
        customer?.let { parts.add(buildCustomerSection(it)) }
        if (!formResponses.isNullOrEmpty()) {
            val sorted = formResponses.sortedBy { it.createdAt }
            if (sorted.size == 1) {
                parts.add(buildFormResponseSection(sorted.first()))
            } else {
                sorted.forEachIndexed { index, response ->
                    val date = response.createdAt?.toLocalDate()?.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""
                    val label = "${response.customerName ?: "Formulário ${index + 1}"} ($date)"
                    parts.add(buildFormResponseSection(response, label))
                }
                parts.add("**IMPORTANTE: Quando houver informações conflitantes entre formulários, priorize os dados do formulário mais recente.**")
            }
        }
        professional?.let { parts.add(buildProfessionalSection(it)) }
        quantitativeData?.let {
            val section = buildQuantitativeDataSection(it)
            if (section.isNotBlank()) parts.add(section)
        }

        return parts.joinToString("\n\n")
    }

    fun buildUserPrompt(sectionType: String, vertical: Vertical? = null): String {
        val instruction = resolveInstruction("section_prompt", vertical)
        if (instruction != null) {
            return instruction.replace("{{SECTION_TYPE}}", sectionType)
        }
        return """Com base nos dados acima, elabore a seção "$sectionType" do laudo. Use APENAS os dados das respostas do formulário e dados quantitativos fornecidos acima para compor o texto. Cada afirmação deve ter fundamentação direta nos dados disponíveis. Responda apenas com o texto da seção, sem JSON, sem aspas, sem formatação."""
    }

    fun buildUserPromptWithContext(
        sectionType: String,
        previousSections: List<PreviousSectionContext>,
        vertical: Vertical? = null
    ): String {
        if (previousSections.isEmpty()) return buildUserPrompt(sectionType, vertical)

        val contextBlock = previousSections.joinToString("\n\n") { prev ->
            "### ${prev.sectionType}\n${prev.summary}"
        }

        val instruction = resolveInstruction("section_prompt_with_context", vertical)
        if (instruction != null) {
            return instruction
                .replace("{{SECTION_TYPE}}", sectionType)
                .replace("{{PREVIOUS_SECTIONS}}", contextBlock)
        }

        return """
            |## Seções já escritas neste laudo (mantenha coerência e não repita informações)
            |
            |$contextBlock
            |
            |---
            |
            |Com base nos dados acima e nas seções já escritas, elabore a seção "$sectionType" do laudo. Use os dados das respostas do formulário e dados quantitativos para compor o texto. Mantenha coerência com as seções anteriores. Cada afirmação deve ter fundamentação direta nos dados disponíveis. Responda apenas com o texto da seção, sem JSON, sem aspas, sem formatação.
        """.trimMargin()
    }

    private fun resolveInstruction(type: String, vertical: Vertical?): String? {
        val instruction = if (vertical != null) {
            aiInstructionPort.findActiveByTypeAndVertical(type, vertical)
                ?: aiInstructionPort.findActiveByType(type)
        } else {
            aiInstructionPort.findActiveByType(type)
        }
        return instruction?.content
    }

    private fun buildTemplateSection(template: ReportTemplate): String {
        val blocksDescription = template.blocks.joinToString("\n") { block ->
            val type = block["type"]?.toString() ?: "unknown"
            val label = block["label"]?.toString() ?: block["content"]?.toString() ?: ""
            "- [$type] $label"
        }
        return """
            |## Estrutura do laudo (template: ${promptSanitizer.sanitize(template.name)})
            |${if (template.description != null) promptSanitizer.sanitize(template.description) else ""}
            |Seções esperadas:
            |$blocksDescription
        """.trimMargin()
    }

    private fun buildCustomerSection(customer: Customer): String {
        val fields = customer.data.entries
            .filter { it.key in SAFE_CUSTOMER_FIELDS }
            .filter { !it.value?.toString().isNullOrBlank() }
            .joinToString("\n") { (key, value) ->
                "- $key: ${promptSanitizer.sanitize(value?.toString() ?: "")}"
            }
        if (fields.isBlank()) return ""
        return """
            |## Dados do paciente/cliente
            |$fields
        """.trimMargin()
    }

    private fun buildFormResponseSection(formResponse: FormResponse): String =
        buildFormResponseSection(formResponse, null)

    private fun buildFormResponseSection(formResponse: FormResponse, sectionLabel: String?): String {
        val answers = formResponse.answers.joinToString("\n") { answer ->
            val label = answer["label"]?.toString() ?: answer["fieldId"]?.toString() ?: ""
            val value = answer["value"]?.toString() ?: ""
            "- ${promptSanitizer.sanitize(label)}: ${promptSanitizer.sanitize(value)}"
        }
        val title = if (sectionLabel != null) {
            "## Respostas do formulário: ${promptSanitizer.sanitize(sectionLabel)}"
        } else {
            "## Respostas do formulário"
        }
        return """
            |$title
            |$answers
        """.trimMargin()
    }

    private fun buildProfessionalSection(professional: ProfessionalSettings): String =
        """
            |## Dados do profissional
            |- Nome: ${promptSanitizer.sanitize(professional.name)}
            |- Registro: ${promptSanitizer.sanitize(professional.crp ?: "")}
            |- Especialização: ${promptSanitizer.sanitize(professional.specialization)}
        """.trimMargin()

    private fun buildQuantitativeDataSection(data: QuantitativeDataPayload): String {
        val parts = mutableListOf<String>()

        data.tables
            .filter { it.dataStatus != "empty" }
            .forEach { table -> parts.add(buildTableContext(table)) }

        data.charts
            .filter { it.dataStatus != "empty" }
            .forEach { chart -> parts.add(buildChartContext(chart)) }

        if (parts.isEmpty()) return ""
        return "## Dados quantitativos do laudo\n\n${parts.joinToString("\n\n")}"
    }

    private fun buildTableContext(table: ComputedTableData): String {
        val statusLabel = if (table.dataStatus == "partial") "[dados parciais]" else "[dados completos]"
        val rows = table.rows.joinToString("\n") { row ->
            val vals = row.values.entries.joinToString(", ") { (col, v) ->
                "$col=${promptSanitizer.sanitize(v)}"
            }
            "- ${promptSanitizer.sanitize(row.label)}: $vals"
        }
        return "### Tabela: ${promptSanitizer.sanitize(table.title)} (${promptSanitizer.sanitize(table.category)}) $statusLabel\n$rows"
    }

    private fun buildChartContext(chart: ComputedChartData): String {
        val statusLabel = if (chart.dataStatus == "partial") "[dados parciais]" else "[dados completos]"
        val seriesText = chart.series.joinToString("\n") { series ->
            val vals = series.values.entries.joinToString(", ") { (cat, v) ->
                "${promptSanitizer.sanitize(cat)}: $v"
            }
            "- ${promptSanitizer.sanitize(series.label)}: $vals"
        }
        return "### Gráfico: ${promptSanitizer.sanitize(chart.title)} $statusLabel\n$seriesText"
    }
}
