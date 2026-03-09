package com.dox.application.port.input

import com.dox.domain.enum.FormResponseStatus
import com.dox.domain.model.Form
import com.dox.domain.model.FormResponse
import java.util.UUID

data class CreateFormCommand(
    val title: String,
    val description: String? = null,
    val fields: List<Map<String, Any?>> = emptyList(),
    val linkedTemplateId: UUID? = null,
    val fieldMappings: Map<String, Any?> = emptyMap()
)

data class UpdateFormCommand(
    val id: UUID,
    val title: String,
    val description: String? = null,
    val fields: List<Map<String, Any?>> = emptyList(),
    val linkedTemplateId: UUID? = null,
    val fieldMappings: Map<String, Any?> = emptyMap()
)

data class CreateFormResponseCommand(
    val formId: UUID,
    val customerId: UUID? = null,
    val customerName: String? = null,
    val answers: Map<String, Any?> = emptyMap()
)

data class UpdateFormResponseCommand(
    val id: UUID,
    val status: FormResponseStatus? = null,
    val answers: Map<String, Any?>? = null
)

interface FormUseCase {
    fun createForm(command: CreateFormCommand): Form
    fun findFormById(id: UUID): Form
    fun findAllForms(): List<Form>
    fun updateForm(command: UpdateFormCommand): Form
    fun deleteForm(id: UUID)

    fun createResponse(command: CreateFormResponseCommand): FormResponse
    fun findResponseById(id: UUID): FormResponse
    fun findResponsesByFormId(formId: UUID): List<FormResponse>
    fun updateResponse(command: UpdateFormResponseCommand): FormResponse
    fun deleteResponse(id: UUID)
}
