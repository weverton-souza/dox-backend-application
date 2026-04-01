package com.dox.application.port.input

import com.dox.domain.enum.FormResponseStatus
import com.dox.domain.model.Form
import com.dox.domain.model.FormResponse
import com.dox.domain.model.FormVersion
import java.util.UUID

data class CreateFormCommand(
    val title: String,
    val description: String? = null,
    val fields: List<Map<String, Any?>> = emptyList(),
    val linkedTemplateId: UUID? = null,
    val fieldMappings: Map<String, Any?> = emptyMap(),
)

data class UpdateFormCommand(
    val id: UUID,
    val title: String,
    val description: String? = null,
    val fields: List<Map<String, Any?>> = emptyList(),
    val linkedTemplateId: UUID? = null,
    val fieldMappings: Map<String, Any?> = emptyMap(),
)

data class CreateFormResponseCommand(
    val formId: UUID,
    val customerId: UUID? = null,
    val customerName: String? = null,
    val answers: List<Map<String, Any?>> = emptyList(),
)

data class UpdateFormResponseCommand(
    val id: UUID,
    val status: FormResponseStatus? = null,
    val answers: List<Map<String, Any?>>? = null,
)

data class FormWithCurrentVersion(
    val form: Form,
    val version: FormVersion,
)

interface FormUseCase {
    fun createForm(command: CreateFormCommand): FormWithCurrentVersion

    fun findFormById(id: UUID): FormWithCurrentVersion

    fun findAllForms(): List<FormWithCurrentVersion>

    fun updateForm(command: UpdateFormCommand): FormWithCurrentVersion

    fun deleteForm(id: UUID)

    fun findVersionsByFormId(formId: UUID): List<FormVersion>

    fun findVersionsByFormIds(formIds: Set<UUID>): List<FormVersion>

    fun findVersion(
        formId: UUID,
        version: Int,
    ): FormVersion

    fun createResponse(command: CreateFormResponseCommand): FormResponse

    fun findResponseById(id: UUID): FormResponse

    fun findResponsesByFormId(formId: UUID): List<FormResponse>

    fun findResponsesByCustomerId(customerId: UUID): List<FormResponse>

    fun updateResponse(command: UpdateFormResponseCommand): FormResponse

    fun deleteResponse(id: UUID)
}
