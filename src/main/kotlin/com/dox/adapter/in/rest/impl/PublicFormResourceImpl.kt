package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.formlink.PublicFormDraftRequest
import com.dox.adapter.`in`.rest.dto.formlink.PublicFormDraftResponse
import com.dox.adapter.`in`.rest.dto.formlink.PublicFormResponse
import com.dox.adapter.`in`.rest.dto.formlink.PublicFormSubmitRequest
import com.dox.adapter.`in`.rest.dto.formlink.PublicFormSubmitResponse
import com.dox.adapter.`in`.rest.resource.PublicFormResource
import com.dox.application.port.input.FormLinkUseCase
import com.dox.application.port.input.PublicFormSubmitCommand
import com.dox.application.port.input.SaveFormDraftCommand
import com.dox.domain.exception.ResourceNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PublicFormResourceImpl(
    private val formLinkUseCase: FormLinkUseCase,
) : PublicFormResource {
    override fun getForm(token: String): ResponseEntity<PublicFormResponse> {
        val data = formLinkUseCase.resolvePublicForm(token)
        return responseEntity(
            PublicFormResponse(
                formTitle = data.formTitle,
                formDescription = data.formDescription,
                fields = data.fields,
                customerName = data.customerName,
                expiresAt = data.expiresAt,
            ),
        )
    }

    override fun submitForm(
        token: String,
        request: PublicFormSubmitRequest,
    ): ResponseEntity<PublicFormSubmitResponse> {
        formLinkUseCase.submitPublicForm(
            PublicFormSubmitCommand(token = token, answers = request.answers),
        )
        return responseEntity(PublicFormSubmitResponse())
    }

    override fun getDraft(token: String): ResponseEntity<PublicFormDraftResponse> {
        val draft =
            formLinkUseCase.findPublicFormDraft(token)
                ?: throw ResourceNotFoundException("Rascunho", token)
        return responseEntity(
            PublicFormDraftResponse(partialResponse = draft.partialResponse, savedAt = draft.savedAt),
        )
    }

    override fun saveDraft(
        token: String,
        request: PublicFormDraftRequest,
    ): ResponseEntity<PublicFormDraftResponse> {
        val saved =
            formLinkUseCase.savePublicFormDraft(
                SaveFormDraftCommand(token = token, partialResponse = request.partialResponse),
            )
        return responseEntity(
            PublicFormDraftResponse(partialResponse = saved.partialResponse, savedAt = saved.savedAt),
        )
    }
}
