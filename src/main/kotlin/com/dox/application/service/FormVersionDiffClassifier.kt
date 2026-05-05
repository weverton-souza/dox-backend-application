package com.dox.application.service

import com.dox.application.port.input.UpdateFormCommand
import com.dox.domain.model.FormVersion
import org.springframework.stereotype.Component

enum class FormDiffKind {
    NONE,
    COSMETIC,
    STRUCTURAL,
}

@Component
class FormVersionDiffClassifier {
    fun classify(
        current: FormVersion,
        command: UpdateFormCommand,
    ): FormDiffKind {
        if (current.scoringConfig != command.scoringConfig) return FormDiffKind.STRUCTURAL

        val fieldsDiff = classifyFields(current.fields, command.fields)
        if (fieldsDiff == FormDiffKind.STRUCTURAL) return FormDiffKind.STRUCTURAL

        val cosmeticChanges =
            fieldsDiff == FormDiffKind.COSMETIC ||
                current.title != command.title ||
                current.description != command.description ||
                current.fieldMappings != command.fieldMappings

        return if (cosmeticChanges) FormDiffKind.COSMETIC else FormDiffKind.NONE
    }

    private fun classifyFields(
        current: List<Map<String, Any?>>,
        incoming: List<Map<String, Any?>>,
    ): FormDiffKind {
        val currentById = current.mapNotNull { it.fieldId()?.let { id -> id to it } }.toMap()
        val incomingById = incoming.mapNotNull { it.fieldId()?.let { id -> id to it } }.toMap()

        if (currentById.keys != incomingById.keys) return FormDiffKind.STRUCTURAL

        var cosmetic = false
        for ((id, currentField) in currentById) {
            val incomingField = incomingById.getValue(id)
            when (compareField(currentField, incomingField)) {
                FormDiffKind.STRUCTURAL -> return FormDiffKind.STRUCTURAL
                FormDiffKind.COSMETIC -> cosmetic = true
                FormDiffKind.NONE -> Unit
            }
        }

        if (!cosmetic && fieldsOrder(current) != fieldsOrder(incoming)) cosmetic = true

        return if (cosmetic) FormDiffKind.COSMETIC else FormDiffKind.NONE
    }

    private fun compareField(
        current: Map<String, Any?>,
        incoming: Map<String, Any?>,
    ): FormDiffKind {
        if (current.fieldType() != incoming.fieldType()) return FormDiffKind.STRUCTURAL
        if (current.fieldRequired() != incoming.fieldRequired()) return FormDiffKind.STRUCTURAL

        val optionsDiff = compareOptions(current.fieldOptions(), incoming.fieldOptions())
        if (optionsDiff == FormDiffKind.STRUCTURAL) return FormDiffKind.STRUCTURAL

        val cosmetic =
            optionsDiff == FormDiffKind.COSMETIC ||
                current.fieldLabel() != incoming.fieldLabel() ||
                current.fieldHelpText() != incoming.fieldHelpText() ||
                current.fieldPlaceholder() != incoming.fieldPlaceholder()

        if (cosmetic) return FormDiffKind.COSMETIC

        val knownKeys = setOf("id", "type", "label", "helpText", "placeholder", "required", "options")
        val currentExtras = current.filterKeys { it !in knownKeys }
        val incomingExtras = incoming.filterKeys { it !in knownKeys }
        if (currentExtras != incomingExtras) return FormDiffKind.STRUCTURAL

        return FormDiffKind.NONE
    }

    private fun compareOptions(
        current: List<Map<String, Any?>>,
        incoming: List<Map<String, Any?>>,
    ): FormDiffKind {
        val currentById = current.mapNotNull { it.optionId()?.let { id -> id to it } }.toMap()
        val incomingById = incoming.mapNotNull { it.optionId()?.let { id -> id to it } }.toMap()

        if (currentById.keys != incomingById.keys) return FormDiffKind.STRUCTURAL

        var cosmetic = false
        for ((id, currentOption) in currentById) {
            val incomingOption = incomingById.getValue(id)
            if (currentOption.optionValue() != incomingOption.optionValue()) return FormDiffKind.STRUCTURAL
            if (currentOption.optionLabel() != incomingOption.optionLabel()) cosmetic = true
        }
        return if (cosmetic) FormDiffKind.COSMETIC else FormDiffKind.NONE
    }

    private fun fieldsOrder(fields: List<Map<String, Any?>>) = fields.mapNotNull { it.fieldId() }

    private fun Map<String, Any?>.fieldId(): String? = (this["id"] as? String)?.takeIf { it.isNotBlank() }

    private fun Map<String, Any?>.fieldType(): String? = this["type"] as? String

    private fun Map<String, Any?>.fieldLabel(): String? = this["label"] as? String

    private fun Map<String, Any?>.fieldHelpText(): String? = this["helpText"] as? String

    private fun Map<String, Any?>.fieldPlaceholder(): String? = this["placeholder"] as? String

    private fun Map<String, Any?>.fieldRequired(): Boolean = this["required"] as? Boolean ?: false

    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any?>.fieldOptions(): List<Map<String, Any?>> = (this["options"] as? List<Map<String, Any?>>) ?: emptyList()

    private fun Map<String, Any?>.optionId(): String? = (this["id"] as? String)?.takeIf { it.isNotBlank() }

    private fun Map<String, Any?>.optionLabel(): String? = this["label"] as? String

    private fun Map<String, Any?>.optionValue(): Any? = this["value"]
}
