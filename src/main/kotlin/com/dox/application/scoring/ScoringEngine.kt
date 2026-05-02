package com.dox.application.scoring

import com.dox.domain.enum.ScoringOperation
import com.dox.domain.scoring.FormFieldAnswer
import com.dox.domain.scoring.FormFieldDef
import com.dox.domain.scoring.ScoreResult
import com.dox.domain.scoring.ScoringClassificationRange
import com.dox.domain.scoring.ScoringConfig
import com.dox.domain.scoring.ScoringFormula
import org.springframework.stereotype.Component

@Component
class ScoringEngine {
    fun calculateAllScores(
        config: ScoringConfig,
        fields: List<FormFieldDef>,
        answers: List<FormFieldAnswer>,
    ): List<ScoreResult> = config.formulas.map { calculateScore(it, fields, answers) }

    fun calculateScore(
        formula: ScoringFormula,
        fields: List<FormFieldDef>,
        answers: List<FormFieldAnswer>,
    ): ScoreResult {
        val fieldsById = fields.associateBy { it.id }
        val answersByField = answers.associateBy { it.fieldId }

        val collected = mutableListOf<Double>()
        for (fid in formula.fieldIds) {
            val field = fieldsById[fid] ?: continue
            collected.addAll(answerValuesForField(field, answersByField[fid]))
        }

        val value = applyOperation(formula.operation, collected)
        val classification =
            if (value != null && formula.classification.isNotEmpty()) {
                classifyValue(value, formula.classification)
            } else {
                null
            }

        return ScoreResult(
            formulaId = formula.id,
            name = formula.name,
            operation = formula.operation,
            value = value,
            classification = classification,
            contributionsCount = collected.size,
        )
    }

    private fun maxValueOfField(field: FormFieldDef): Int =
        when (field.type) {
            "inventory-item" -> field.options.maxOfOrNull { it.value ?: 0 } ?: 0
            "likert-matrix" -> field.likertScale.maxOfOrNull { it.value } ?: 0
            "scale" -> field.scaleMax
            else -> 0
        }

    private fun answerValuesForField(
        field: FormFieldDef,
        answer: FormFieldAnswer?,
    ): List<Double> {
        if (answer == null) return emptyList()

        return when (field.type) {
            "inventory-item" -> {
                val selectedId = answer.selectedOptionIds.firstOrNull() ?: return emptyList()
                val opt = field.options.firstOrNull { it.id == selectedId }
                val rawValue = opt?.value ?: return emptyList()
                val finalValue = if (field.reverseScored) maxValueOfField(field) - rawValue else rawValue
                listOf(finalValue.toDouble())
            }
            "likert-matrix" -> {
                val max = maxValueOfField(field)
                field.likertRows.mapNotNull { row ->
                    val v = answer.likertAnswers[row.id] ?: return@mapNotNull null
                    val finalValue = if (row.reverseScored) max - v else v
                    finalValue.toDouble()
                }
            }
            "scale" -> answer.scaleValue?.let { listOf(it.toDouble()) } ?: emptyList()
            else -> emptyList()
        }
    }

    private fun applyOperation(
        operation: ScoringOperation,
        values: List<Double>,
    ): Double? {
        if (values.isEmpty()) return if (operation == ScoringOperation.COUNT) 0.0 else null

        return when (operation) {
            ScoringOperation.SUM -> values.sum()
            ScoringOperation.MEAN -> values.sum() / values.size
            ScoringOperation.MIN -> values.min()
            ScoringOperation.MAX -> values.max()
            ScoringOperation.COUNT -> values.size.toDouble()
        }
    }

    private fun classifyValue(
        value: Double,
        ranges: List<ScoringClassificationRange>,
    ): String? = ranges.firstOrNull { value >= it.min && value <= it.max }?.label
}
