package com.dox.application.port.input

import com.dox.domain.model.FormLink
import com.dox.domain.model.FormResponse
import java.time.LocalDateTime
import java.util.UUID

data class CreateFormLinkCommand(
    val formId: UUID,
    val customerId: UUID,
    val expiresInHours: Long = 72
)

data class PublicFormSubmitCommand(
    val token: String,
    val answers: List<Map<String, Any?>> = emptyList()
)

data class PublicFormData(
    val formTitle: String,
    val formDescription: String?,
    val fields: List<Map<String, Any?>>,
    val customerName: String?,
    val expiresAt: LocalDateTime
)

data class FormLinkWithToken(
    val formLink: FormLink,
    val token: String
)

interface FormLinkUseCase {
    fun createFormLink(command: CreateFormLinkCommand): FormLinkWithToken

    fun findFormLinksByTenant(): List<FormLinkWithToken>

    fun findFormLinksByCustomer(customerId: UUID): List<FormLinkWithToken>

    fun revokeFormLink(id: UUID)

    fun resolvePublicForm(token: String): PublicFormData

    fun submitPublicForm(command: PublicFormSubmitCommand): FormResponse
}
