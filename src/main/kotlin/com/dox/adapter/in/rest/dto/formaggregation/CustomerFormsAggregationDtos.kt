package com.dox.adapter.`in`.rest.dto.formaggregation

import com.dox.domain.enum.FormLinkStatus
import com.dox.domain.enum.RespondentType
import com.dox.domain.enum.ScoringOperation
import java.time.LocalDateTime
import java.util.UUID

data class FormSummaryResponse(
    val id: UUID,
    val title: String,
)

data class FormVersionSummaryResponse(
    val id: UUID,
    val versionMajor: Int,
    val versionMinor: Int,
    val versionLabel: String,
    val title: String,
)

data class AggregatedRespondentResponse(
    val linkId: UUID,
    val responseId: UUID?,
    val respondentType: RespondentType,
    val respondentName: String?,
    val customerContactId: UUID?,
    val relationType: String?,
    val status: FormLinkStatus,
    val submittedAt: LocalDateTime?,
    val expiresAt: LocalDateTime,
)

data class AggregatedFormGroupResponse(
    val form: FormSummaryResponse,
    val version: FormVersionSummaryResponse,
    val sentAt: LocalDateTime,
    val respondents: List<AggregatedRespondentResponse>,
)

data class ScoreResultResponse(
    val formulaId: String,
    val name: String,
    val operation: ScoringOperation,
    val value: Double?,
    val classification: String?,
    val contributionsCount: Int,
)

data class ComparisonRespondentResponse(
    val linkId: UUID,
    val responseId: UUID?,
    val respondentType: RespondentType,
    val respondentName: String?,
    val customerContactId: UUID?,
    val relationType: String?,
    val status: FormLinkStatus,
    val submittedAt: LocalDateTime?,
    val answers: List<Map<String, Any?>>,
    val scoreBreakdown: List<ScoreResultResponse>,
)

data class ComparisonResponse(
    val form: FormSummaryResponse,
    val version: FormVersionSummaryResponse,
    val fields: List<Map<String, Any?>>,
    val scoringConfig: Map<String, Any?>,
    val respondents: List<ComparisonRespondentResponse>,
)
