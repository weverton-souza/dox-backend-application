package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.form.FormRequest
import com.dox.adapter.`in`.rest.dto.form.FormResponseDto
import com.dox.adapter.`in`.rest.dto.form.FormResponseRequest
import com.dox.adapter.`in`.rest.dto.form.FormResponseResponseDto
import com.dox.adapter.`in`.rest.dto.form.FormVersionResponseDto
import com.dox.adapter.`in`.rest.resource.FormResource
import com.dox.application.port.input.CreateFormCommand
import com.dox.application.port.input.CreateFormResponseCommand
import com.dox.application.port.input.FormUseCase
import com.dox.application.port.input.FormWithCurrentVersion
import com.dox.application.port.input.UpdateFormCommand
import com.dox.application.port.input.UpdateFormResponseCommand
import com.dox.domain.model.FormResponse
import com.dox.domain.model.FormVersion
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class FormResourceImpl(
    private val formUseCase: FormUseCase
) : FormResource {

    override fun findAll(): ResponseEntity<List<FormResponseDto>> =
        responseEntity(formUseCase.findAllForms().map { it.toResponse() })

    override fun create(request: FormRequest): ResponseEntity<FormResponseDto> =
        responseEntity(
            formUseCase.createForm(
                CreateFormCommand(
                    title = request.title, description = request.description,
                    fields = request.fields, linkedTemplateId = request.linkedTemplateId,
                    fieldMappings = request.fieldMappings
                )
            ).toResponse(),
            HttpStatus.CREATED
        )

    override fun findById(id: UUID): ResponseEntity<FormResponseDto> =
        responseEntity(formUseCase.findFormById(id).toResponse())

    override fun update(id: UUID, request: FormRequest): ResponseEntity<FormResponseDto> =
        responseEntity(
            formUseCase.updateForm(
                UpdateFormCommand(
                    id = id, title = request.title, description = request.description,
                    fields = request.fields, linkedTemplateId = request.linkedTemplateId,
                    fieldMappings = request.fieldMappings
                )
            ).toResponse()
        )

    override fun deleteForm(id: UUID): ResponseEntity<Void> {
        formUseCase.deleteForm(id)
        return noContent()
    }

    override fun getVersions(id: UUID): ResponseEntity<List<FormVersionResponseDto>> =
        responseEntity(formUseCase.findVersionsByFormId(id).map { it.toResponse() })

    override fun getVersion(id: UUID, version: Int): ResponseEntity<FormVersionResponseDto> =
        responseEntity(formUseCase.findVersion(id, version).toResponse())

    override fun getResponses(id: UUID): ResponseEntity<List<FormResponseResponseDto>> {
        val versions = formUseCase.findVersionsByFormId(id).associateBy { it.id }
        return responseEntity(
            formUseCase.findResponsesByFormId(id).map { it.toResponse(versions[it.formVersionId]?.version) }
        )
    }

    override fun createResponse(id: UUID, request: FormResponseRequest): ResponseEntity<FormResponseResponseDto> =
        responseEntity(
            formUseCase.createResponse(
                CreateFormResponseCommand(
                    formId = id, customerId = request.customerId,
                    customerName = request.customerName, answers = request.answers
                )
            ).toResponse(null),
            HttpStatus.CREATED
        )

    override fun getResponse(id: UUID, responseId: UUID): ResponseEntity<FormResponseResponseDto> =
        responseEntity(formUseCase.findResponseById(responseId).toResponse(null))

    override fun updateResponse(
        id: UUID,
        responseId: UUID,
        request: FormResponseRequest
    ): ResponseEntity<FormResponseResponseDto> =
        responseEntity(
            formUseCase.updateResponse(
                UpdateFormResponseCommand(
                    id = responseId, status = request.status, answers = request.answers
                )
            ).toResponse(null)
        )

    override fun deleteResponse(id: UUID, responseId: UUID): ResponseEntity<Void> {
        formUseCase.deleteResponse(responseId)
        return noContent()
    }

    override fun getResponsesByCustomer(customerId: UUID): ResponseEntity<List<FormResponseResponseDto>> {
        val responses = formUseCase.findResponsesByCustomerId(customerId)
        val formIds = responses.map { it.formId }.toSet()
        val versionsByFormId = formIds.flatMap { formUseCase.findVersionsByFormId(it) }
            .associateBy { it.id }
        return responseEntity(
            responses.map { it.toResponse(versionsByFormId[it.formVersionId]?.version) }
        )
    }

    private fun FormWithCurrentVersion.toResponse() = FormResponseDto(
        id = form.id,
        title = version.title,
        description = version.description,
        fields = version.fields,
        linkedTemplateId = form.linkedTemplateId,
        fieldMappings = version.fieldMappings,
        currentVersion = form.currentVersion,
        createdAt = form.createdAt,
        updatedAt = form.updatedAt
    )

    private fun FormVersion.toResponse() = FormVersionResponseDto(
        id, formId, version, title, description, fields, fieldMappings, createdAt
    )

    private fun FormResponse.toResponse(versionNumber: Int?) = FormResponseResponseDto(
        id, formId, formVersionId, customerId, customerName, status, answers, generatedReportId, versionNumber, createdAt, updatedAt
    )
}
