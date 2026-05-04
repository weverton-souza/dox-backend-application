package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.formlink.PublicFormResponse
import com.dox.adapter.`in`.rest.dto.formlink.PublicFormSubmitRequest
import com.dox.adapter.`in`.rest.dto.formlink.PublicFormSubmitResponse
import com.dox.adapter.`in`.rest.resource.PublicFormResource
import com.dox.application.port.input.FormLinkUseCase
import com.dox.application.port.input.PublicFormSubmitCommand
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
                respondentName = data.respondentName,
                respondentType = data.respondentType,
                expiresAt = data.expiresAt,
            ),
        )
    }

    override fun submitForm(
        token: String,
        request: PublicFormSubmitRequest,
    ): ResponseEntity<PublicFormSubmitResponse> {
        formLinkUseCase.submitPublicForm(
            PublicFormSubmitCommand(
                token = token,
                answers = request.answers,
                pageDurationsMs = request.pageDurationsMs,
            ),
        )
        return responseEntity(PublicFormSubmitResponse())
    }
}
