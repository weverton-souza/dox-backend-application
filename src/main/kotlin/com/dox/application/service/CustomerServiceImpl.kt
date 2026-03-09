package com.dox.application.service

import com.dox.application.port.input.CreateCustomerCommand
import com.dox.application.port.input.CreateCustomerEventCommand
import com.dox.application.port.input.CreateCustomerNoteCommand
import com.dox.application.port.input.CustomerUseCase
import com.dox.application.port.input.UpdateCustomerCommand
import com.dox.application.port.output.CustomerPersistencePort
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.Customer
import com.dox.domain.model.CustomerEvent
import com.dox.domain.model.CustomerNote
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CustomerServiceImpl(
    private val customerPersistencePort: CustomerPersistencePort
) : CustomerUseCase {

    @Transactional
    override fun create(command: CreateCustomerCommand): Customer =
        customerPersistencePort.save(Customer(data = command.data))

    override fun findById(id: UUID): Customer =
        customerPersistencePort.findById(id)
            ?: throw ResourceNotFoundException("Cliente não encontrado")

    override fun findAll(search: String?, pageable: Pageable): Page<Customer> =
        if (search.isNullOrBlank()) customerPersistencePort.findAll(pageable)
        else customerPersistencePort.search(search, pageable)

    @Transactional
    override fun update(command: UpdateCustomerCommand): Customer {
        customerPersistencePort.findById(command.id)
            ?: throw ResourceNotFoundException("Cliente não encontrado")
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
    override fun deleteEvent(eventId: UUID) {
        customerPersistencePort.deleteEvent(eventId)
    }
}
