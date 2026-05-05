package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.FormJpaEntity
import com.dox.adapter.out.persistence.entity.FormResponseJpaEntity
import com.dox.adapter.out.persistence.entity.FormVersionJpaEntity
import com.dox.adapter.out.persistence.repository.FormJpaRepository
import com.dox.adapter.out.persistence.repository.FormResponseJpaRepository
import com.dox.adapter.out.persistence.repository.FormVersionJpaRepository
import com.dox.application.port.output.FormPersistencePort
import com.dox.domain.model.Form
import com.dox.domain.model.FormResponse
import com.dox.domain.model.FormVersion
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FormPersistenceAdapter(
    private val formJpaRepository: FormJpaRepository,
    private val formVersionJpaRepository: FormVersionJpaRepository,
    private val formResponseJpaRepository: FormResponseJpaRepository,
) : FormPersistencePort {
    override fun saveForm(form: Form): Form {
        val entity =
            formJpaRepository.findById(form.id).orElse(null)
                ?: FormJpaEntity().apply { id = form.id }
        entity.currentMajor = form.currentMajor
        entity.currentMinor = form.currentMinor
        return formJpaRepository.save(entity).toDomain()
    }

    override fun findFormById(id: UUID): Form? = formJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findAllForms(): List<Form> = formJpaRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt")).map { it.toDomain() }

    override fun deleteForm(id: UUID) = formJpaRepository.deleteById(id)

    override fun saveVersion(version: FormVersion): FormVersion {
        val entity =
            formVersionJpaRepository.findById(version.id).orElse(null)
                ?: FormVersionJpaEntity().apply { id = version.id }
        entity.formId = version.formId
        entity.versionMajor = version.versionMajor
        entity.versionMinor = version.versionMinor
        entity.title = version.title
        entity.description = version.description
        entity.fields = version.fields
        entity.fieldMappings = version.fieldMappings
        entity.scoringConfig = version.scoringConfig
        return formVersionJpaRepository.save(entity).toDomain()
    }

    override fun findVersionById(id: UUID): FormVersion? = formVersionJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findVersionsByFormId(formId: UUID): List<FormVersion> = formVersionJpaRepository.findByFormId(formId).map { it.toDomain() }

    override fun findVersionsByFormIds(formIds: Set<UUID>): List<FormVersion> = formVersionJpaRepository.findByFormIdIn(formIds).map { it.toDomain() }

    override fun findVersionByFormIdAndMajorMinor(
        formId: UUID,
        major: Int,
        minor: Int,
    ): FormVersion? = formVersionJpaRepository.findByFormIdAndVersionMajorAndVersionMinor(formId, major, minor)?.toDomain()

    override fun saveResponse(response: FormResponse): FormResponse {
        val entity =
            formResponseJpaRepository.findById(response.id).orElse(null)
                ?: FormResponseJpaEntity().apply { id = response.id }
        entity.formId = response.formId
        entity.formVersionId = response.formVersionId
        entity.customerId = response.customerId
        entity.customerName = response.customerName
        entity.customerContactId = response.customerContactId
        entity.respondentType = response.respondentType
        entity.respondentName = response.respondentName
        entity.status = response.status
        entity.answers = response.answers
        entity.additionalEvaluators = response.additionalEvaluators
        entity.pageDurationsMs = response.pageDurationsMs
        entity.generatedReportId = response.generatedReportId
        return formResponseJpaRepository.save(entity).toDomain()
    }

    override fun findResponseById(id: UUID): FormResponse? = formResponseJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findResponsesByFormId(formId: UUID): List<FormResponse> = formResponseJpaRepository.findByFormId(formId).map { it.toDomain() }

    override fun findResponsesByCustomerId(customerId: UUID): List<FormResponse> = formResponseJpaRepository.findByCustomerIdOrderByUpdatedAtDesc(customerId).map { it.toDomain() }

    override fun countResponsesByFormVersionId(formVersionId: UUID): Long = formResponseJpaRepository.countByFormVersionId(formVersionId)

    override fun countResponsesByFormId(formId: UUID): Long = formResponseJpaRepository.countByFormId(formId)

    override fun findResponsesByIds(ids: List<UUID>): List<FormResponse> = formResponseJpaRepository.findAllByIdIn(ids).map { it.toDomain() }

    override fun findResponsesByCustomerAndFormVersion(
        customerId: UUID,
        formId: UUID,
        formVersionId: UUID,
    ): List<FormResponse> =
        formResponseJpaRepository
            .findByCustomerIdAndFormIdAndFormVersionId(customerId, formId, formVersionId)
            .map { it.toDomain() }

    override fun deleteResponse(id: UUID) = formResponseJpaRepository.deleteById(id)

    private fun FormJpaEntity.toDomain() =
        Form(
            id = id,
            currentMajor = currentMajor,
            currentMinor = currentMinor,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    private fun FormVersionJpaEntity.toDomain() =
        FormVersion(
            id = id,
            formId = formId,
            versionMajor = versionMajor,
            versionMinor = versionMinor,
            title = title,
            description = description,
            fields = fields,
            fieldMappings = fieldMappings,
            scoringConfig = scoringConfig,
            createdAt = createdAt,
        )

    private fun FormResponseJpaEntity.toDomain() =
        FormResponse(
            id = id,
            formId = formId,
            formVersionId = formVersionId,
            customerId = customerId,
            customerName = customerName,
            customerContactId = customerContactId,
            respondentType = respondentType,
            respondentName = respondentName,
            status = status,
            answers = answers,
            additionalEvaluators = additionalEvaluators,
            pageDurationsMs = pageDurationsMs,
            generatedReportId = generatedReportId,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
