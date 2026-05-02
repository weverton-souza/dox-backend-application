package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.formlink.CreateFormLinkRequest
import com.dox.adapter.`in`.rest.dto.formlink.FormLinkResponse
import com.dox.adapter.`in`.rest.dto.formlink.MultiSendRequest
import com.dox.adapter.`in`.rest.dto.formlink.RespondentInfoResponse
import com.dox.adapter.`in`.rest.resource.FormLinkResource
import com.dox.application.port.input.CreateFormLinkCommand
import com.dox.application.port.input.FormLinkUseCase
import com.dox.application.port.input.FormLinkWithToken
import com.dox.application.port.input.MultiSendCommand
import com.dox.application.port.input.RecipientSpec
import com.dox.config.security.RequiresModule
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequiresModule("forms")
class FormLinkResourceImpl(
    private val formLinkUseCase: FormLinkUseCase,
) : FormLinkResource {
    override fun create(request: CreateFormLinkRequest): ResponseEntity<FormLinkResponse> =
        responseEntity(
            formLinkUseCase.createFormLink(
                CreateFormLinkCommand(
                    formId = request.formId,
                    customerId = request.customerId,
                    expiresInHours = request.expiresInHours,
                ),
            ).toResponse(),
            HttpStatus.CREATED,
        )

    override fun multiSend(request: MultiSendRequest): ResponseEntity<List<FormLinkResponse>> =
        responseEntity(
            formLinkUseCase.multiSend(
                MultiSendCommand(
                    formId = request.formId,
                    customerId = request.customerId,
                    expiresInHours = request.expiresInHours,
                    recipients =
                        request.recipients.map {
                            RecipientSpec(
                                respondentType = it.respondentType,
                                customerContactId = it.customerContactId,
                            )
                        },
                ),
            ).map { it.toResponse() },
            HttpStatus.CREATED,
        )

    override fun findAll(customerId: UUID?): ResponseEntity<List<FormLinkResponse>> {
        val links =
            if (customerId != null) {
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

    private fun FormLinkWithToken.toResponse() =
        FormLinkResponse(
            id = formLink.id,
            token = token,
            formId = formLink.formId,
            formVersionId = formLink.formVersionId,
            customerId = formLink.customerId,
            customerContactId = formLink.customerContactId,
            respondentType = formLink.respondentType,
            respondent =
                RespondentInfoResponse(
                    type = respondent.type,
                    name = respondent.name,
                    customerContactId = respondent.customerContactId,
                    relationType = respondent.relationType,
                ),
            status = formLink.status,
            expiresAt = formLink.expiresAt,
            createdAt = formLink.createdAt,
            updatedAt = formLink.updatedAt,
        )
}
