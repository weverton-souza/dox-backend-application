package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.assessment.AssessmentEntryRequest
import com.dox.adapter.`in`.rest.dto.assessment.AssessmentEntryResponse
import com.dox.adapter.`in`.rest.dto.assessment.AssessmentRequest
import com.dox.adapter.`in`.rest.dto.assessment.AssessmentResponse
import com.dox.adapter.`in`.rest.dto.assessment.AssessmentScoreDto
import com.dox.adapter.`in`.rest.dto.assessment.RelatedTemplateResponse
import com.dox.adapter.`in`.rest.resource.AssessmentResource
import com.dox.application.port.input.AssessmentEntryCommand
import com.dox.application.port.input.AssessmentScoreCommand
import com.dox.application.port.input.AssessmentUseCase
import com.dox.application.port.input.CreateAssessmentCommand
import com.dox.application.port.input.RelatedTemplate
import com.dox.application.port.input.UpdateAssessmentCommand
import com.dox.domain.model.Assessment
import com.dox.domain.model.AssessmentEntry
import com.dox.domain.model.AssessmentScore
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class AssessmentResourceImpl(
    private val assessmentUseCase: AssessmentUseCase,
) : AssessmentResource {
    override fun list(
        customerId: UUID,
        parameters: Map<String, Any>,
    ): ResponseEntity<Page<AssessmentResponse>> {
        val pageable = retrievePageableParameter(parameters)
        val page = assessmentUseCase.findByCustomerId(customerId, pageable)
        return responseEntity(
            PageImpl(
                page.content.map { it.toResponse() },
                pageable,
                page.totalElements,
            ),
        )
    }

    override fun create(
        customerId: UUID,
        request: AssessmentRequest,
    ): ResponseEntity<AssessmentResponse> =
        responseEntity(
            assessmentUseCase.create(
                CreateAssessmentCommand(
                    customerId = customerId,
                    appointmentId = request.appointmentId,
                    title = request.title,
                    category = request.category,
                    appliedAt = request.appliedAt,
                    notes = request.notes,
                    parentAssessmentId = request.parentAssessmentId,
                    entries = request.entries.map { it.toCommand() },
                ),
            ).toResponse(),
            HttpStatus.CREATED,
        )

    override fun findById(
        customerId: UUID,
        id: UUID,
    ): ResponseEntity<AssessmentResponse> = responseEntity(assessmentUseCase.findById(id).toResponse())

    override fun update(
        customerId: UUID,
        id: UUID,
        request: AssessmentRequest,
    ): ResponseEntity<AssessmentResponse> =
        responseEntity(
            assessmentUseCase.update(
                UpdateAssessmentCommand(
                    id = id,
                    customerId = customerId,
                    appointmentId = request.appointmentId,
                    title = request.title,
                    category = request.category,
                    appliedAt = request.appliedAt,
                    notes = request.notes,
                    parentAssessmentId = request.parentAssessmentId,
                    entries = request.entries.map { it.toCommand() },
                ),
            ).toResponse(),
        )

    override fun delete(
        customerId: UUID,
        id: UUID,
    ): ResponseEntity<Void> {
        assessmentUseCase.delete(id)
        return noContent()
    }

    override fun autocomplete(q: String): ResponseEntity<List<String>> = responseEntity(assessmentUseCase.autocompleteInstruments(q))

    override fun relatedTemplates(
        customerId: UUID,
        id: UUID,
    ): ResponseEntity<List<RelatedTemplateResponse>> =
        responseEntity(
            assessmentUseCase.findRelatedTemplates(id).map { it.toResponse() },
        )

    private fun AssessmentEntryRequest.toCommand() =
        AssessmentEntryCommand(
            id = id,
            instrumentName = instrumentName,
            entryType = entryType,
            orderIndex = orderIndex,
            scores = scores.map { AssessmentScoreCommand(it.index, it.label, it.value, it.classification) },
            block = block,
            observations = observations,
            attachmentFileId = attachmentFileId,
        )

    private fun Assessment.toResponse() =
        AssessmentResponse(
            id = id,
            customerId = customerId,
            appointmentId = appointmentId,
            applierId = applierId,
            title = title,
            category = category,
            appliedAt = appliedAt,
            notes = notes,
            parentAssessmentId = parentAssessmentId,
            professionalDeclarationAcceptedAt = professionalDeclarationAcceptedAt,
            professionalDeclarationRevision = professionalDeclarationRevision,
            entries = entries.map { it.toResponse() },
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    private fun AssessmentEntry.toResponse() =
        AssessmentEntryResponse(
            id = id,
            instrumentName = instrumentName,
            entryType = entryType,
            orderIndex = orderIndex,
            scores = scores.map { it.toDto() },
            block = block,
            observations = observations,
            attachmentFileId = attachmentFileId,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    private fun AssessmentScore.toDto() = AssessmentScoreDto(index = index, label = label, value = value, classification = classification)

    private fun RelatedTemplate.toResponse() =
        RelatedTemplateResponse(
            id = id,
            name = name,
            type = type,
            instrumentName = instrumentName,
            category = category,
        )
}
