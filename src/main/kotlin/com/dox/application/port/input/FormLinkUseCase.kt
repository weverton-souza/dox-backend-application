package com.dox.application.port.input

import com.dox.domain.enum.RespondentType
import com.dox.domain.model.FormLink
import com.dox.domain.model.FormResponse
import java.time.LocalDateTime
import java.util.UUID

data class CreateFormLinkCommand(
    val formId: UUID,
    val customerId: UUID,
    val expiresInHours: Long = 72,
)

data class RecipientSpec(
    val respondentType: RespondentType,
    val customerContactId: UUID? = null,
)

data class MultiSendCommand(
    val formId: UUID,
    val customerId: UUID,
    val expiresInHours: Long = 168,
    val recipients: List<RecipientSpec>,
)

data class PublicFormSubmitCommand(
    val token: String,
    val answers: List<Map<String, Any?>> = emptyList(),
    val pageDurationsMs: Map<String, Long> = emptyMap(),
)

data class PublicFormData(
    val formTitle: String,
    val formDescription: String?,
    val fields: List<Map<String, Any?>>,
    val customerName: String?,
    val respondentName: String?,
    val respondentType: RespondentType,
    val expiresAt: LocalDateTime,
)

data class RespondentInfo(
    val type: RespondentType,
    val name: String?,
    val customerContactId: UUID? = null,
    val relationType: String? = null,
)

data class FormLinkWithToken(
    val formLink: FormLink,
    val token: String,
    val respondent: RespondentInfo,
)

interface FormLinkUseCase {
    fun createFormLink(command: CreateFormLinkCommand): FormLinkWithToken

    fun multiSend(command: MultiSendCommand): List<FormLinkWithToken>

    fun findFormLinksByTenant(): List<FormLinkWithToken>

    fun findFormLinksByCustomer(customerId: UUID): List<FormLinkWithToken>

    fun revokeFormLink(id: UUID)

    fun resolvePublicForm(token: String): PublicFormData

    fun submitPublicForm(command: PublicFormSubmitCommand): FormResponse
}
