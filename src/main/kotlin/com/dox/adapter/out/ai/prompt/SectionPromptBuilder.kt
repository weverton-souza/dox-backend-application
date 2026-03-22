package com.dox.adapter.out.ai.prompt

import com.dox.domain.model.Customer
import com.dox.domain.model.FormResponse
import com.dox.domain.model.ProfessionalSettings
import com.dox.domain.model.ReportTemplate
import org.springframework.stereotype.Component

@Component
class SectionPromptBuilder(
    private val promptSanitizer: PromptSanitizer
) {

    fun buildContext(
        customer: Customer?,
        formResponse: FormResponse?,
        template: ReportTemplate?,
        professional: ProfessionalSettings?
    ): String {
        val parts = mutableListOf<String>()

        template?.let {
            parts.add(buildTemplateSection(it))
        }

        customer?.let {
            parts.add(buildCustomerSection(it))
        }

        formResponse?.let {
            parts.add(buildFormResponseSection(it))
        }

        professional?.let {
            parts.add(buildProfessionalSection(it))
        }

        return parts.joinToString("\n\n")
    }

    fun buildUserPrompt(sectionType: String): String =
        """Com base nos dados acima, elabore a seção "$sectionType" do laudo. Use TODOS os dados das respostas do formulário fornecidos acima para compor o texto. Responda apenas com o texto da seção, sem JSON, sem aspas, sem formatação."""

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
        val fields = customer.data.entries.joinToString("\n") { (key, value) ->
            "- $key: ${promptSanitizer.sanitize(value?.toString() ?: "")}"
        }
        return """
            |## Dados do paciente/cliente
            |$fields
        """.trimMargin()
    }

    private fun buildFormResponseSection(formResponse: FormResponse): String {
        val answers = formResponse.answers.joinToString("\n") { answer ->
            val label = answer["label"]?.toString() ?: answer["fieldId"]?.toString() ?: ""
            val value = answer["value"]?.toString() ?: ""
            "- ${promptSanitizer.sanitize(label)}: ${promptSanitizer.sanitize(value)}"
        }
        return """
            |## Respostas do formulário
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
}
