package com.dox.application.scoring

import com.dox.domain.enum.ScoringOperation
import com.dox.domain.scoring.FormFieldAnswer
import com.dox.domain.scoring.FormFieldDef
import com.dox.domain.scoring.FormFieldOption
import com.dox.domain.scoring.LikertRow
import com.dox.domain.scoring.LikertScalePoint
import com.dox.domain.scoring.ScoringClassificationRange
import com.dox.domain.scoring.ScoringConfig
import com.dox.domain.scoring.ScoringFormula
import org.springframework.stereotype.Component

@Component
class ScoringMapper {
    @Suppress("UNCHECKED_CAST")
    fun parseFields(raw: List<Map<String, Any?>>): List<FormFieldDef> =
        raw.mapNotNull { fieldMap ->
            val id = fieldMap["id"]?.toString() ?: return@mapNotNull null
            val type = fieldMap["type"]?.toString() ?: return@mapNotNull null
            FormFieldDef(
                id = id,
                type = type,
                label = fieldMap["label"]?.toString() ?: "",
                reverseScored = fieldMap["reverseScored"] as? Boolean ?: false,
                options = parseOptions(fieldMap["options"] as? List<Map<String, Any?>>),
                likertScale = parseLikertScale(fieldMap["likertScale"] as? List<Map<String, Any?>>),
                likertRows = parseLikertRows(fieldMap["likertRows"] as? List<Map<String, Any?>>),
                scaleMin = (fieldMap["scaleMin"] as? Number)?.toInt() ?: 0,
                scaleMax = (fieldMap["scaleMax"] as? Number)?.toInt() ?: 0,
            )
        }

    @Suppress("UNCHECKED_CAST")
    fun parseAnswers(raw: List<Map<String, Any?>>): List<FormFieldAnswer> =
        raw.mapNotNull { answerMap ->
            val fieldId = answerMap["fieldId"]?.toString() ?: return@mapNotNull null
            FormFieldAnswer(
                fieldId = fieldId,
                value = answerMap["value"]?.toString(),
                selectedOptionIds =
                    (answerMap["selectedOptionIds"] as? List<*>)?.mapNotNull { it?.toString() }
                        ?: emptyList(),
                scaleValue = (answerMap["scaleValue"] as? Number)?.toInt(),
                likertAnswers =
                    (answerMap["likertAnswers"] as? Map<String, Any?>)
                        ?.mapNotNull { (k, v) -> (v as? Number)?.toInt()?.let { k to it } }
                        ?.toMap()
                        ?: emptyMap(),
            )
        }

    @Suppress("UNCHECKED_CAST")
    fun parseScoringConfig(raw: Map<String, Any?>): ScoringConfig {
        val formulas = raw["formulas"] as? List<Map<String, Any?>> ?: return ScoringConfig()
        return ScoringConfig(
            formulas =
                formulas.mapNotNull { f ->
                    val id = f["id"]?.toString() ?: return@mapNotNull null
                    val name = f["name"]?.toString() ?: ""
                    val operation = parseOperation(f["operation"]?.toString()) ?: return@mapNotNull null
                    ScoringFormula(
                        id = id,
                        name = name,
                        operation = operation,
                        fieldIds = (f["fieldIds"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList(),
                        classification = parseClassification(f["classification"] as? List<Map<String, Any?>>),
                    )
                },
        )
    }

    private fun parseOptions(raw: List<Map<String, Any?>>?): List<FormFieldOption> =
        raw.orEmpty().mapNotNull { opt ->
            val id = opt["id"]?.toString() ?: return@mapNotNull null
            FormFieldOption(
                id = id,
                label = opt["label"]?.toString() ?: "",
                value = (opt["value"] as? Number)?.toInt(),
            )
        }

    private fun parseLikertScale(raw: List<Map<String, Any?>>?): List<LikertScalePoint> =
        raw.orEmpty().mapNotNull { point ->
            val value = (point["value"] as? Number)?.toInt() ?: return@mapNotNull null
            LikertScalePoint(value = value, label = point["label"]?.toString() ?: "")
        }

    private fun parseLikertRows(raw: List<Map<String, Any?>>?): List<LikertRow> =
        raw.orEmpty().mapNotNull { row ->
            val id = row["id"]?.toString() ?: return@mapNotNull null
            LikertRow(
                id = id,
                label = row["label"]?.toString() ?: "",
                reverseScored = row["reverseScored"] as? Boolean ?: false,
            )
        }

    private fun parseClassification(raw: List<Map<String, Any?>>?): List<ScoringClassificationRange> =
        raw.orEmpty().mapNotNull { range ->
            val min = (range["min"] as? Number)?.toDouble() ?: return@mapNotNull null
            val max = (range["max"] as? Number)?.toDouble() ?: return@mapNotNull null
            ScoringClassificationRange(min = min, max = max, label = range["label"]?.toString() ?: "")
        }

    private fun parseOperation(raw: String?): ScoringOperation? =
        raw?.let {
            runCatching { ScoringOperation.valueOf(it.uppercase()) }.getOrNull()
        }
}
