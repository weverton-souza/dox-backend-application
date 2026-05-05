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
@Transactional(readOnly = true)
class FormServiceImpl(
    private val formPersistencePort: FormPersistencePort,
    private val diffClassifier: FormVersionDiffClassifier,
) : FormUseCase {
    @Transactional
    override fun createForm(command: CreateFormCommand): FormWithCurrentVersion {
        val form = formPersistencePort.saveForm(Form())
        val version =
            formPersistencePort.saveVersion(
                FormVersion(
                    formId = form.id,
                    versionMajor = form.currentMajor,
                    versionMinor = form.currentMinor,
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
        return FormWithCurrentVersion(form, loadCurrentVersion(form))
    }

    override fun findAllForms(): List<FormWithCurrentVersion> {
        val forms = formPersistencePort.findAllForms()
        if (forms.isEmpty()) return emptyList()

        val formIds = forms.map { it.id }.toSet()
        val versionsByFormId =
            formPersistencePort.findVersionsByFormIds(formIds)
                .groupBy { it.formId }

        return forms.map { form ->
            val version =
                versionsByFormId[form.id]
                    ?.firstOrNull { it.versionMajor == form.currentMajor && it.versionMinor == form.currentMinor }
                    ?: throw ResourceNotFoundException("Versão do formulário", "${form.id}:v${form.currentVersionLabel}")
            FormWithCurrentVersion(form, version)
        }
    }

    @Transactional
    override fun updateForm(command: UpdateFormCommand): FormWithCurrentVersion {
        val form =
            formPersistencePort.findFormById(command.id)
                ?: throw ResourceNotFoundException("Formulário", command.id.toString())

        val currentVersion = loadCurrentVersion(form)

        val totalResponses = formPersistencePort.countResponsesByFormId(form.id)
        if (totalResponses == 0L) {
            return FormWithCurrentVersion(form, updateVersionInPlace(currentVersion, command))
        }

        return when (diffClassifier.classify(currentVersion, command)) {
            FormDiffKind.NONE -> FormWithCurrentVersion(form, currentVersion)
            FormDiffKind.COSMETIC -> bumpMinor(form, command)
            FormDiffKind.STRUCTURAL -> bumpMajor(form, command)
        }
    }

    private fun updateVersionInPlace(
        currentVersion: FormVersion,
        command: UpdateFormCommand,
    ): FormVersion =
        formPersistencePort.saveVersion(
            currentVersion.copy(
                title = command.title,
                description = command.description,
                fields = command.fields,
                fieldMappings = command.fieldMappings,
                scoringConfig = command.scoringConfig,
            ),
        )

    private fun bumpMinor(
        form: Form,
        command: UpdateFormCommand,
    ): FormWithCurrentVersion {
        val newMinor = form.currentMinor + 1
        val newVersion =
            formPersistencePort.saveVersion(
                FormVersion(
                    formId = form.id,
                    versionMajor = form.currentMajor,
                    versionMinor = newMinor,
                    title = command.title,
                    description = command.description,
                    fields = command.fields,
                    fieldMappings = command.fieldMappings,
                    scoringConfig = command.scoringConfig,
                ),
            )
        val updatedForm = formPersistencePort.saveForm(form.copy(currentMinor = newMinor))
        return FormWithCurrentVersion(updatedForm, newVersion)
    }

    private fun bumpMajor(
        form: Form,
        command: UpdateFormCommand,
    ): FormWithCurrentVersion {
        val newMajor = form.currentMajor + 1
        val newVersion =
            formPersistencePort.saveVersion(
                FormVersion(
                    formId = form.id,
                    versionMajor = newMajor,
                    versionMinor = 0,
                    title = command.title,
                    description = command.description,
                    fields = command.fields,
                    fieldMappings = command.fieldMappings,
                    scoringConfig = command.scoringConfig,
                ),
            )
        val updatedForm = formPersistencePort.saveForm(form.copy(currentMajor = newMajor, currentMinor = 0))
        return FormWithCurrentVersion(updatedForm, newVersion)
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
        major: Int,
        minor: Int,
    ): FormVersion =
        formPersistencePort.findVersionByFormIdAndMajorMinor(formId, major, minor)
            ?: throw ResourceNotFoundException("Versão do formulário", "$formId:v$major.$minor")

    @Transactional
    override fun createResponse(command: CreateFormResponseCommand): FormResponse {
        val form =
            formPersistencePort.findFormById(command.formId)
                ?: throw ResourceNotFoundException("Formulário", command.formId.toString())
        val resolvedVersionId =
            command.formVersionId ?: loadCurrentVersion(form).id
        return formPersistencePort.saveResponse(
            FormResponse(
                formId = command.formId,
                formVersionId = resolvedVersionId,
                customerId = command.customerId,
                customerName = command.customerName,
                customerContactId = command.customerContactId,
                respondentType = command.respondentType,
                respondentName = command.respondentName,
                answers = command.answers,
                additionalEvaluators = command.additionalEvaluators,
                pageDurationsMs = command.pageDurationsMs,
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
                additionalEvaluators = command.additionalEvaluators ?: existing.additionalEvaluators,
                pageDurationsMs = command.pageDurationsMs ?: existing.pageDurationsMs,
            ),
        )
    }

    @Transactional
    override fun deleteResponse(id: UUID) {
        formPersistencePort.findResponseById(id)
            ?: throw ResourceNotFoundException("Resposta", id.toString())
        formPersistencePort.deleteResponse(id)
    }

    private fun loadCurrentVersion(form: Form): FormVersion =
        formPersistencePort.findVersionByFormIdAndMajorMinor(form.id, form.currentMajor, form.currentMinor)
            ?: throw ResourceNotFoundException("Versão do formulário", "${form.id}:v${form.currentVersionLabel}")
}
