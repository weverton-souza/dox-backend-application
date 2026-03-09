package com.dox.application.service

import com.dox.application.port.input.CreateFormCommand
import com.dox.application.port.input.CreateFormResponseCommand
import com.dox.application.port.input.FormUseCase
import com.dox.application.port.input.UpdateFormCommand
import com.dox.application.port.input.UpdateFormResponseCommand
import com.dox.application.port.output.FormPersistencePort
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.Form
import com.dox.domain.model.FormResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class FormServiceImpl(
    private val formPersistencePort: FormPersistencePort
) : FormUseCase {

    @Transactional
    override fun createForm(command: CreateFormCommand): Form =
        formPersistencePort.saveForm(
            Form(
                title = command.title,
                description = command.description,
                fields = command.fields,
                linkedTemplateId = command.linkedTemplateId,
                fieldMappings = command.fieldMappings
            )
        )

    override fun findFormById(id: UUID): Form =
        formPersistencePort.findFormById(id)
            ?: throw ResourceNotFoundException("Formulário não encontrado")

    override fun findAllForms(): List<Form> = formPersistencePort.findAllForms()

    @Transactional
    override fun updateForm(command: UpdateFormCommand): Form {
        formPersistencePort.findFormById(command.id)
            ?: throw ResourceNotFoundException("Formulário não encontrado")
        return formPersistencePort.saveForm(
            Form(
                id = command.id,
                title = command.title,
                description = command.description,
                fields = command.fields,
                linkedTemplateId = command.linkedTemplateId,
                fieldMappings = command.fieldMappings
            )
        )
    }

    @Transactional
    override fun deleteForm(id: UUID) = formPersistencePort.deleteForm(id)

    @Transactional
    override fun createResponse(command: CreateFormResponseCommand): FormResponse =
        formPersistencePort.saveResponse(
            FormResponse(
                formId = command.formId,
                customerId = command.customerId,
                customerName = command.customerName,
                answers = command.answers
            )
        )

    override fun findResponseById(id: UUID): FormResponse =
        formPersistencePort.findResponseById(id)
            ?: throw ResourceNotFoundException("Resposta não encontrada")

    override fun findResponsesByFormId(formId: UUID): List<FormResponse> =
        formPersistencePort.findResponsesByFormId(formId)

    @Transactional
    override fun updateResponse(command: UpdateFormResponseCommand): FormResponse {
        val existing = formPersistencePort.findResponseById(command.id)
            ?: throw ResourceNotFoundException("Resposta não encontrada")
        return formPersistencePort.saveResponse(
            existing.copy(
                status = command.status ?: existing.status,
                answers = command.answers ?: existing.answers
            )
        )
    }

    @Transactional
    override fun deleteResponse(id: UUID) = formPersistencePort.deleteResponse(id)
}
