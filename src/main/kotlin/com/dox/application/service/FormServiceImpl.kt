package com.dox.application.service

import com.dox.application.port.input.CreateFormCommand
import com.dox.application.port.input.CreateFormResponseCommand
import com.dox.application.port.input.FormUseCase
import com.dox.application.port.input.FormWithCurrentVersion
import com.dox.application.port.input.UpdateFormCommand
import com.dox.application.port.input.UpdateFormResponseCommand
import com.dox.application.port.output.FormPersistencePort
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.Form
import com.dox.domain.model.FormResponse
import com.dox.domain.model.FormVersion
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class FormServiceImpl(
    private val formPersistencePort: FormPersistencePort,
) : FormUseCase {
    @Transactional
    override fun createForm(command: CreateFormCommand): FormWithCurrentVersion {
        val form =
            formPersistencePort.saveForm(
                Form(linkedTemplateId = command.linkedTemplateId),
            )
        val version =
            formPersistencePort.saveVersion(
                FormVersion(
                    formId = form.id,
                    version = 1,
                    title = command.title,
                    description = command.description,
                    fields = command.fields,
                    fieldMappings = command.fieldMappings,
                    scoringConfig = command.scoringConfig,
                ),
            )
        return FormWithCurrentVersion(form, version)
    }

    override fun findFormById(id: UUID): FormWithCurrentVersion {
        val form =
            formPersistencePort.findFormById(id)
                ?: throw ResourceNotFoundException("Formulário", id.toString())
        val version =
            formPersistencePort.findVersionByFormIdAndVersion(id, form.currentVersion)
                ?: throw ResourceNotFoundException("Versão do formulário", "$id:v${form.currentVersion}")
        return FormWithCurrentVersion(form, version)
    }

    override fun findAllForms(): List<FormWithCurrentVersion> {
        val forms = formPersistencePort.findAllForms()
        return forms.map { form ->
            val version =
                formPersistencePort.findVersionByFormIdAndVersion(form.id, form.currentVersion)
                    ?: throw ResourceNotFoundException("Versão do formulário", "${form.id}:v${form.currentVersion}")
            FormWithCurrentVersion(form, version)
        }
    }

    @Transactional
    override fun updateForm(command: UpdateFormCommand): FormWithCurrentVersion {
        val form =
            formPersistencePort.findFormById(command.id)
                ?: throw ResourceNotFoundException("Formulário", command.id.toString())

        val currentVersion =
            formPersistencePort.findVersionByFormIdAndVersion(form.id, form.currentVersion)
                ?: throw ResourceNotFoundException("Versão do formulário", "${form.id}:v${form.currentVersion}")

        val responseCount = formPersistencePort.countResponsesByFormVersionId(currentVersion.id)

        val updatedForm =
            formPersistencePort.saveForm(
                form.copy(linkedTemplateId = command.linkedTemplateId),
            )

        return if (responseCount == 0L) {
            updateVersionInPlace(updatedForm, currentVersion, command)
        } else {
            createNewVersion(updatedForm, command)
        }
    }

    private fun updateVersionInPlace(
        form: Form,
        currentVersion: FormVersion,
        command: UpdateFormCommand,
    ): FormWithCurrentVersion {
        val updatedVersion =
            formPersistencePort.saveVersion(
                currentVersion.copy(
                    title = command.title,
                    description = command.description,
                    fields = command.fields,
                    fieldMappings = command.fieldMappings,
                    scoringConfig = command.scoringConfig,
                ),
            )
        return FormWithCurrentVersion(form, updatedVersion)
    }

    private fun createNewVersion(
        form: Form,
        command: UpdateFormCommand,
    ): FormWithCurrentVersion {
        val newVersionNumber = form.currentVersion + 1
        val newVersion =
            formPersistencePort.saveVersion(
                FormVersion(
                    formId = form.id,
                    version = newVersionNumber,
                    title = command.title,
                    description = command.description,
                    fields = command.fields,
                    fieldMappings = command.fieldMappings,
                    scoringConfig = command.scoringConfig,
                ),
            )
        val formWithNewVersion =
            formPersistencePort.saveForm(
                form.copy(currentVersion = newVersionNumber),
            )
        return FormWithCurrentVersion(formWithNewVersion, newVersion)
    }

    @Transactional
    override fun deleteForm(id: UUID) {
        formPersistencePort.findFormById(id)
            ?: throw ResourceNotFoundException("Formulário", id.toString())
        formPersistencePort.deleteForm(id)
    }

    override fun findVersionsByFormId(formId: UUID): List<FormVersion> {
        formPersistencePort.findFormById(formId)
            ?: throw ResourceNotFoundException("Formulário", formId.toString())
        return formPersistencePort.findVersionsByFormId(formId)
    }

    override fun findVersionsByFormIds(formIds: Set<UUID>): List<FormVersion> = formPersistencePort.findVersionsByFormIds(formIds)

    override fun findVersion(
        formId: UUID,
        version: Int,
    ): FormVersion =
        formPersistencePort.findVersionByFormIdAndVersion(formId, version)
            ?: throw ResourceNotFoundException("Versão do formulário", "$formId:v$version")

    @Transactional
    override fun createResponse(command: CreateFormResponseCommand): FormResponse {
        val form =
            formPersistencePort.findFormById(command.formId)
                ?: throw ResourceNotFoundException("Formulário", command.formId.toString())
        val currentVersion =
            formPersistencePort.findVersionByFormIdAndVersion(form.id, form.currentVersion)
                ?: throw ResourceNotFoundException("Versão do formulário", "${form.id}:v${form.currentVersion}")
        return formPersistencePort.saveResponse(
            FormResponse(
                formId = command.formId,
                formVersionId = currentVersion.id,
                customerId = command.customerId,
                customerName = command.customerName,
                answers = command.answers,
            ),
        )
    }

    override fun findResponseById(id: UUID): FormResponse =
        formPersistencePort.findResponseById(id)
            ?: throw ResourceNotFoundException("Resposta", id.toString())

    override fun findResponsesByFormId(formId: UUID): List<FormResponse> = formPersistencePort.findResponsesByFormId(formId)

    override fun findResponsesByCustomerId(customerId: UUID): List<FormResponse> = formPersistencePort.findResponsesByCustomerId(customerId)

    @Transactional
    override fun updateResponse(command: UpdateFormResponseCommand): FormResponse {
        val existing =
            formPersistencePort.findResponseById(command.id)
                ?: throw ResourceNotFoundException("Resposta", command.id.toString())
        return formPersistencePort.saveResponse(
            existing.copy(
                status = command.status ?: existing.status,
                answers = command.answers ?: existing.answers,
            ),
        )
    }

    @Transactional
    override fun deleteResponse(id: UUID) {
        formPersistencePort.findResponseById(id)
            ?: throw ResourceNotFoundException("Resposta", id.toString())
        formPersistencePort.deleteResponse(id)
    }
}
