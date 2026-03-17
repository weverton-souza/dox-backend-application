package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.form.FormRequest
import com.dox.adapter.`in`.rest.dto.form.FormResponseDto
import com.dox.adapter.`in`.rest.dto.form.FormResponseRequest
import com.dox.adapter.`in`.rest.dto.form.FormResponseResponseDto
import com.dox.adapter.`in`.rest.resource.FormResource
import com.dox.application.port.input.CreateFormCommand
import com.dox.application.port.input.CreateFormResponseCommand
import com.dox.application.port.input.FormUseCase
import com.dox.application.port.input.UpdateFormCommand
import com.dox.application.port.input.UpdateFormResponseCommand
import com.dox.domain.model.Form
import com.dox.domain.model.FormResponse
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

    override fun getResponses(id: UUID): ResponseEntity<List<FormResponseResponseDto>> =
        responseEntity(formUseCase.findResponsesByFormId(id).map { it.toResponse() })

    override fun createResponse(id: UUID, request: FormResponseRequest): ResponseEntity<FormResponseResponseDto> =
        responseEntity(
            formUseCase.createResponse(
                CreateFormResponseCommand(
                    formId = id, customerId = request.customerId,
                    customerName = request.customerName, answers = request.answers
                )
            ).toResponse(),
            HttpStatus.CREATED
        )

    override fun getResponse(id: UUID, responseId: UUID): ResponseEntity<FormResponseResponseDto> =
        responseEntity(formUseCase.findResponseById(responseId).toResponse())

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
            ).toResponse()
        )

    override fun deleteResponse(id: UUID, responseId: UUID): ResponseEntity<Void> {
        formUseCase.deleteResponse(responseId)
        return noContent()
    }

    private fun Form.toResponse() = FormResponseDto(
        id, title, description, fields, linkedTemplateId, fieldMappings, createdAt, updatedAt
    )

    private fun FormResponse.toResponse() = FormResponseResponseDto(
        id, formId, customerId, customerName, status, answers, generatedReportId, createdAt, updatedAt
    )
}
