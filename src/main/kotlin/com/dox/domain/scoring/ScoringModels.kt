package com.dox.domain.scoring

import com.dox.domain.enum.ScoringOperation

data class FormFieldOption(
    val id: String,
    val label: String,
    val value: Int? = null,
)

data class LikertScalePoint(
    val value: Int,
    val label: String,
)

data class LikertRow(
    val id: String,
    val label: String,
    val reverseScored: Boolean = false,
)

data class FormFieldDef(
    val id: String,
    val type: String,
    val label: String,
    val reverseScored: Boolean = false,
    val options: List<FormFieldOption> = emptyList(),
    val likertScale: List<LikertScalePoint> = emptyList(),
    val likertRows: List<LikertRow> = emptyList(),
    val scaleMin: Int = 0,
    val scaleMax: Int = 0,
)

data class FormFieldAnswer(
    val fieldId: String,
    val value: String? = null,
    val selectedOptionIds: List<String> = emptyList(),
    val scaleValue: Int? = null,
    val likertAnswers: Map<String, Int> = emptyMap(),
)

data class ScoringClassificationRange(
    val min: Double,
    val max: Double,
    val label: String,
)

data class ScoringFormula(
    val id: String,
    val name: String,
    val operation: ScoringOperation,
    val fieldIds: List<String>,
    val classification: List<ScoringClassificationRange> = emptyList(),
)

data class ScoringConfig(
    val formulas: List<ScoringFormula> = emptyList(),
)

data class ScoreResult(
    val formulaId: String,
    val name: String,
    val operation: ScoringOperation,
    val value: Double?,
    val classification: String?,
    val contributionsCount: Int,
)
