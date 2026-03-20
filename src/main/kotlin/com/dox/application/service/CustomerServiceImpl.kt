package com.dox.application.service

import com.dox.application.port.input.CreateCustomerCommand
import com.dox.application.port.input.CreateCustomerEventCommand
import com.dox.application.port.input.CreateCustomerNoteCommand
import com.dox.application.port.input.CustomerUseCase
import com.dox.application.port.input.UpdateCustomerCommand
import com.dox.application.port.input.UpdateCustomerEventCommand
import com.dox.application.port.output.CustomerPersistencePort
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.Customer
import com.dox.domain.validation.CnpjValidator
import com.dox.domain.validation.CpfValidator
import com.dox.domain.model.CustomerEvent
import com.dox.domain.model.CustomerNote
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class CustomerServiceImpl(
    private val customerPersistencePort: CustomerPersistencePort
) : CustomerUseCase {

    @Transactional
    override fun create(command: CreateCustomerCommand): Customer {
        validateDocuments(command.data)
        return customerPersistencePort.save(Customer(data = command.data))
    }

    override fun findById(id: UUID): Customer =
        customerPersistencePort.findById(id)
            ?: throw ResourceNotFoundException("Cliente", id.toString())

    override fun findAll(search: String?, pageable: Pageable): Page<Customer> =
        if (search.isNullOrBlank()) customerPersistencePort.findAll(pageable)
        else customerPersistencePort.search(search, pageable)

    @Transactional
    override fun update(command: UpdateCustomerCommand): Customer {
        customerPersistencePort.findById(command.id)
            ?: throw ResourceNotFoundException("Cliente", command.id.toString())
        validateDocuments(command.data)
        return customerPersistencePort.save(Customer(id = command.id, data = command.data))
    }

    @Transactional
    override fun delete(id: UUID) {
        customerPersistencePort.softDelete(id)
    }

    override fun getNotes(customerId: UUID): List<CustomerNote> =
        customerPersistencePort.findNotesByCustomerId(customerId)

    @Transactional
    override fun addNote(command: CreateCustomerNoteCommand): CustomerNote =
        customerPersistencePort.saveNote(
            CustomerNote(customerId = command.customerId, content = command.content)
        )

    @Transactional
    override fun deleteNote(noteId: UUID) {
        customerPersistencePort.deleteNote(noteId)
    }

    override fun getEvents(customerId: UUID): List<CustomerEvent> =
        customerPersistencePort.findEventsByCustomerId(customerId)

    @Transactional
    override fun addEvent(command: CreateCustomerEventCommand): CustomerEvent =
        customerPersistencePort.saveEvent(
            CustomerEvent(
                customerId = command.customerId,
                type = command.type,
                title = command.title,
                description = command.description,
                date = command.date
            )
        )

    @Transactional
    override fun updateEvent(command: UpdateCustomerEventCommand): CustomerEvent {
        customerPersistencePort.findEventById(command.id)
            ?: throw ResourceNotFoundException("Evento", command.id.toString())
        return customerPersistencePort.saveEvent(
            CustomerEvent(
                id = command.id,
                customerId = command.customerId,
                type = command.type,
                title = command.title,
                description = command.description,
                date = command.date
            )
        )
    }

    @Transactional
    override fun deleteEvent(eventId: UUID) {
        customerPersistencePort.deleteEvent(eventId)
    }

    private fun validateDocuments(data: Map<String, Any?>) {
        val cpf = data["cpf"]?.toString()
        if (!cpf.isNullOrBlank() && !CpfValidator.isValidCpf(cpf)) {
            throw BusinessException("CPF inválido")
        }

        val cnpj = data["cnpj"]?.toString()
        if (!cnpj.isNullOrBlank() && !CnpjValidator.isValidCnpj(cnpj)) {
            throw BusinessException("CNPJ inválido")
        }
    }

    override fun findAllEventsByDateRange(from: LocalDateTime, to: LocalDateTime): List<Pair<CustomerEvent, String>> {
        val events = customerPersistencePort.findEventsByDateRange(from, to)
        if (events.isEmpty()) return emptyList()

        val customerIds = events.map { it.customerId }.toSet()
        val customerMap = customerPersistencePort.findByIds(customerIds)
            .associateBy { it.id }

        return events.map { event ->
            val customerName = customerMap[event.customerId]
                ?.data?.get("name")?.toString() ?: ""
            event to customerName
        }
    }
}
