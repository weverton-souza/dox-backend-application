package com.dox.application.port.output

import com.dox.domain.model.Form
import com.dox.domain.model.FormResponse
import com.dox.domain.model.FormVersion
import java.util.UUID

interface FormPersistencePort {
    fun saveForm(form: Form): Form

    fun findFormById(id: UUID): Form?

    fun findAllForms(): List<Form>

    fun deleteForm(id: UUID)

    fun saveVersion(version: FormVersion): FormVersion

    fun findVersionById(id: UUID): FormVersion?

    fun findVersionsByFormId(formId: UUID): List<FormVersion>

    fun findVersionsByFormIds(formIds: Set<UUID>): List<FormVersion>

    fun findVersionByFormIdAndVersion(
        formId: UUID,
        version: Int,
    ): FormVersion?

    fun saveResponse(response: FormResponse): FormResponse

    fun findResponseById(id: UUID): FormResponse?

    fun findResponsesByFormId(formId: UUID): List<FormResponse>

    fun findResponsesByCustomerId(customerId: UUID): List<FormResponse>

    fun countResponsesByFormVersionId(formVersionId: UUID): Long

    fun findResponsesByIds(ids: List<UUID>): List<FormResponse>

    fun deleteResponse(id: UUID)
}
