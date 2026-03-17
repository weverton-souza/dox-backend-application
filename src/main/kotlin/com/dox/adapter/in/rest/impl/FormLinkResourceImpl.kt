package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.formlink.CreateFormLinkRequest
import com.dox.adapter.`in`.rest.dto.formlink.FormLinkResponse
import com.dox.adapter.`in`.rest.resource.FormLinkResource
import com.dox.application.port.input.CreateFormLinkCommand
import com.dox.application.port.input.FormLinkUseCase
import com.dox.application.port.input.FormLinkWithToken
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class FormLinkResourceImpl(
    private val formLinkUseCase: FormLinkUseCase
) : FormLinkResource {

    override fun create(request: CreateFormLinkRequest): ResponseEntity<FormLinkResponse> =
        responseEntity(
            formLinkUseCase.createFormLink(
                CreateFormLinkCommand(
                    formId = request.formId,
                    customerId = request.customerId,
                    expiresInHours = request.expiresInHours
                )
            ).toResponse(),
            HttpStatus.CREATED
        )

    override fun findAll(customerId: UUID?): ResponseEntity<List<FormLinkResponse>> {
        val links = if (customerId != null) {
            formLinkUseCase.findFormLinksByCustomer(customerId)
        } else {
            formLinkUseCase.findFormLinksByTenant()
        }
        return responseEntity(links.map { it.toResponse() })
    }

    override fun revoke(id: UUID): ResponseEntity<Void> {
        formLinkUseCase.revokeFormLink(id)
        return noContent()
    }

    private fun FormLinkWithToken.toResponse() = FormLinkResponse(
        formLink.id, token, formLink.formId, formLink.customerId,
        formLink.status, formLink.expiresAt, formLink.createdAt, formLink.updatedAt
    )
}
