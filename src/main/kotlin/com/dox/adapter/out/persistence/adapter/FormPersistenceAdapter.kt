package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.FormJpaEntity
import com.dox.adapter.out.persistence.entity.FormResponseJpaEntity
import com.dox.adapter.out.persistence.repository.FormJpaRepository
import com.dox.adapter.out.persistence.repository.FormResponseJpaRepository
import com.dox.application.port.output.FormPersistencePort
import com.dox.domain.model.Form
import com.dox.domain.model.FormResponse
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FormPersistenceAdapter(
    private val formJpaRepository: FormJpaRepository,
    private val formResponseJpaRepository: FormResponseJpaRepository
) : FormPersistencePort {

    override fun saveForm(form: Form): Form {
        val entity = formJpaRepository.findById(form.id).orElse(null)
            ?: FormJpaEntity().apply { id = form.id }
        entity.title = form.title
        entity.description = form.description
        entity.fields = form.fields
        entity.linkedTemplateId = form.linkedTemplateId
        entity.fieldMappings = form.fieldMappings
        return formJpaRepository.save(entity).toDomain()
    }

    override fun findFormById(id: UUID): Form? =
        formJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findAllForms(): List<Form> =
        formJpaRepository.findAll().map { it.toDomain() }

    override fun deleteForm(id: UUID) = formJpaRepository.deleteById(id)

    override fun saveResponse(response: FormResponse): FormResponse {
        val entity = formResponseJpaRepository.findById(response.id).orElse(null)
            ?: FormResponseJpaEntity().apply { id = response.id }
        entity.formId = response.formId
        entity.customerId = response.customerId
        entity.customerName = response.customerName
        entity.status = response.status
        entity.answers = response.answers
        entity.generatedReportId = response.generatedReportId
        return formResponseJpaRepository.save(entity).toDomain()
    }

    override fun findResponseById(id: UUID): FormResponse? =
        formResponseJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findResponsesByFormId(formId: UUID): List<FormResponse> =
        formResponseJpaRepository.findByFormId(formId).map { it.toDomain() }

    override fun deleteResponse(id: UUID) = formResponseJpaRepository.deleteById(id)

    private fun FormJpaEntity.toDomain() = Form(
        id, title, description, fields, linkedTemplateId, fieldMappings, createdAt, updatedAt
    )

    private fun FormResponseJpaEntity.toDomain() = FormResponse(
        id, formId, customerId, customerName, status, answers, generatedReportId, createdAt, updatedAt
    )
}
