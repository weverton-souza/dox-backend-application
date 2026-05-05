package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.formaggregation.AggregatedFormGroupResponse
import com.dox.adapter.`in`.rest.dto.formaggregation.AggregatedRespondentResponse
import com.dox.adapter.`in`.rest.dto.formaggregation.ComparisonRespondentResponse
import com.dox.adapter.`in`.rest.dto.formaggregation.ComparisonResponse
import com.dox.adapter.`in`.rest.dto.formaggregation.FormSummaryResponse
import com.dox.adapter.`in`.rest.dto.formaggregation.FormVersionSummaryResponse
import com.dox.adapter.`in`.rest.dto.formaggregation.ScoreResultResponse
import com.dox.adapter.`in`.rest.resource.CustomerFormsResource
import com.dox.application.port.input.AggregatedFormGroup
import com.dox.application.port.input.AggregatedRespondent
import com.dox.application.port.input.ComparisonRespondent
import com.dox.application.port.input.ComparisonResult
import com.dox.application.port.input.CustomerFormsAggregationUseCase
import com.dox.application.port.input.FormSummary
import com.dox.application.port.input.FormVersionSummary
import com.dox.config.security.RequiresModule
import com.dox.domain.scoring.ScoreResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequiresModule("forms")
class CustomerFormsResourceImpl(
    private val useCase: CustomerFormsAggregationUseCase,
) : CustomerFormsResource {
    override fun aggregated(customerId: UUID): ResponseEntity<List<AggregatedFormGroupResponse>> =
        responseEntity(
            useCase.aggregatedByCustomer(customerId).map { it.toResponse() },
        )

    override fun comparison(
        customerId: UUID,
        formId: UUID,
        versionId: UUID,
    ): ResponseEntity<ComparisonResponse> =
        responseEntity(
            useCase.comparison(customerId, formId, versionId).toResponse(),
        )

    private fun AggregatedFormGroup.toResponse(): AggregatedFormGroupResponse =
        AggregatedFormGroupResponse(
            form = form.toResponse(),
            version = version.toResponse(),
            sentAt = sentAt,
            respondents = respondents.map { it.toResponse() },
        )

    private fun AggregatedRespondent.toResponse(): AggregatedRespondentResponse =
        AggregatedRespondentResponse(
            linkId = linkId,
            responseId = responseId,
            respondentType = respondentType,
            respondentName = respondentName,
            customerContactId = customerContactId,
            relationType = relationType,
            status = status,
            submittedAt = submittedAt,
            expiresAt = expiresAt,
        )

    private fun ComparisonResult.toResponse(): ComparisonResponse =
        ComparisonResponse(
            form = form.toResponse(),
            version = version.toResponse(),
            fields = fields,
            scoringConfig = scoringConfig,
            respondents = respondents.map { it.toResponse() },
        )

    private fun ComparisonRespondent.toResponse(): ComparisonRespondentResponse =
        ComparisonRespondentResponse(
            linkId = linkId,
            responseId = responseId,
            respondentType = respondentType,
            respondentName = respondentName,
            customerContactId = customerContactId,
            relationType = relationType,
            status = status,
            submittedAt = submittedAt,
            answers = answers,
            scoreBreakdown = scoreBreakdown.map { it.toResponse() },
        )

    private fun ScoreResult.toResponse(): ScoreResultResponse =
        ScoreResultResponse(
            formulaId = formulaId,
            name = name,
            operation = operation,
            value = value,
            classification = classification,
            contributionsCount = contributionsCount,
        )

    private fun FormSummary.toResponse(): FormSummaryResponse = FormSummaryResponse(id = id, title = title)

    private fun FormVersionSummary.toResponse(): FormVersionSummaryResponse =
        FormVersionSummaryResponse(
            id = id,
            versionMajor = versionMajor,
            versionMinor = versionMinor,
            versionLabel = versionLabel,
            title = title,
        )
}
