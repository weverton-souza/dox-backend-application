package com.dox.application.port.output

import com.dox.domain.model.Form
import com.dox.domain.model.FormResponse
import java.util.UUID

interface FormPersistencePort {
    fun saveForm(form: Form): Form
    fun findFormById(id: UUID): Form?
    fun findAllForms(): List<Form>
    fun deleteForm(id: UUID)

    fun saveResponse(response: FormResponse): FormResponse
    fun findResponseById(id: UUID): FormResponse?
    fun findResponsesByFormId(formId: UUID): List<FormResponse>
    fun deleteResponse(id: UUID)
}
