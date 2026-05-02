package com.dox.application.port.output

import com.dox.domain.model.Customer
import com.dox.domain.model.CustomerEvent
import com.dox.domain.model.CustomerNote
import com.dox.domain.model.PatientContact
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.util.UUID

interface CustomerPersistencePort {
    fun save(customer: Customer): Customer

    fun findById(id: UUID): Customer?

    fun findAll(pageable: Pageable): Page<Customer>

    fun search(
        query: String,
        pageable: Pageable,
    ): Page<Customer>

    fun softDelete(id: UUID)

    fun saveNote(note: CustomerNote): CustomerNote

    fun findNoteById(noteId: UUID): CustomerNote?

    fun findNotesByCustomerId(customerId: UUID): List<CustomerNote>

    fun deleteNote(noteId: UUID)

    fun saveEvent(event: CustomerEvent): CustomerEvent

    fun findEventById(eventId: UUID): CustomerEvent?

    fun findEventsByCustomerId(customerId: UUID): List<CustomerEvent>

    fun deleteEvent(eventId: UUID)

    fun findByIds(ids: Set<UUID>): List<Customer>

    fun findEventsByDateRange(
        from: LocalDateTime,
        to: LocalDateTime,
    ): List<CustomerEvent>

    fun saveContact(contact: PatientContact): PatientContact

    fun findContactById(contactId: UUID): PatientContact?

    fun findContactsByCustomerId(customerId: UUID): List<PatientContact>

    fun deleteContact(contactId: UUID)
}
