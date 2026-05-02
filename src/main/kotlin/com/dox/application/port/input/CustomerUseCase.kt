package com.dox.application.port.input

import com.dox.domain.enum.CustomerContactRelationType
import com.dox.domain.model.Customer
import com.dox.domain.model.CustomerContact
import com.dox.domain.model.CustomerEvent
import com.dox.domain.model.CustomerNote
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.util.UUID

data class CreateCustomerCommand(val data: Map<String, Any?>)

data class UpdateCustomerCommand(val id: UUID, val data: Map<String, Any?>)

data class CreateCustomerNoteCommand(val customerId: UUID, val content: String)

data class CreateCustomerEventCommand(
    val customerId: UUID,
    val type: String,
    val title: String,
    val description: String?,
    val date: LocalDateTime,
)

data class UpdateCustomerEventCommand(
    val id: UUID,
    val customerId: UUID,
    val type: String,
    val title: String,
    val description: String?,
    val date: LocalDateTime,
)

data class CreateCustomerContactCommand(
    val customerId: UUID,
    val name: String,
    val relationType: CustomerContactRelationType,
    val email: String? = null,
    val phone: String? = null,
    val notes: String? = null,
    val canReceiveForms: Boolean = true,
)

data class UpdateCustomerContactCommand(
    val id: UUID,
    val customerId: UUID,
    val name: String,
    val relationType: CustomerContactRelationType,
    val email: String? = null,
    val phone: String? = null,
    val notes: String? = null,
    val canReceiveForms: Boolean = true,
)

interface CustomerUseCase {
    fun create(command: CreateCustomerCommand): Customer

    fun findById(id: UUID): Customer

    fun findAll(
        search: String?,
        pageable: Pageable,
    ): Page<Customer>

    fun update(command: UpdateCustomerCommand): Customer

    fun delete(id: UUID)

    fun getNotes(customerId: UUID): List<CustomerNote>

    fun addNote(command: CreateCustomerNoteCommand): CustomerNote

    fun deleteNote(noteId: UUID)

    fun getEvents(customerId: UUID): List<CustomerEvent>

    fun addEvent(command: CreateCustomerEventCommand): CustomerEvent

    fun updateEvent(command: UpdateCustomerEventCommand): CustomerEvent

    fun deleteEvent(eventId: UUID)

    fun findAllEventsByDateRange(
        from: LocalDateTime,
        to: LocalDateTime,
    ): List<Pair<CustomerEvent, String>>

    fun getContacts(customerId: UUID): List<CustomerContact>

    fun addContact(command: CreateCustomerContactCommand): CustomerContact

    fun updateContact(command: UpdateCustomerContactCommand): CustomerContact

    fun deleteContact(contactId: UUID)
}
