package com.dox.application.port.input

import com.dox.domain.enum.FormLinkStatus
import com.dox.domain.enum.RespondentType
import com.dox.domain.scoring.ScoreResult
import java.time.LocalDateTime
import java.util.UUID

data class FormSummary(
    val id: UUID,
    val title: String,
)

data class FormVersionSummary(
    val id: UUID,
    val version: Int,
    val title: String,
)

data class AggregatedRespondent(
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

data class AggregatedFormGroup(
    val form: FormSummary,
    val version: FormVersionSummary,
    val sentAt: LocalDateTime,
    val respondents: List<AggregatedRespondent>,
)

data class ComparisonRespondent(
    val linkId: UUID,
    val responseId: UUID?,
    val respondentType: RespondentType,
    val respondentName: String?,
    val customerContactId: UUID?,
    val relationType: String?,
    val status: FormLinkStatus,
    val submittedAt: LocalDateTime?,
    val answers: List<Map<String, Any?>>,
    val scoreBreakdown: List<ScoreResult>,
)

data class ComparisonResult(
    val form: FormSummary,
    val version: FormVersionSummary,
    val fields: List<Map<String, Any?>>,
    val scoringConfig: Map<String, Any?>,
    val respondents: List<ComparisonRespondent>,
)

interface CustomerFormsAggregationUseCase {
    fun aggregatedByCustomer(customerId: UUID): List<AggregatedFormGroup>

    fun comparison(
        customerId: UUID,
        formId: UUID,
        formVersionId: UUID,
    ): ComparisonResult
}
