package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.CustomerEventJpaEntity
import com.dox.adapter.out.persistence.entity.CustomerJpaEntity
import com.dox.adapter.out.persistence.entity.CustomerNoteJpaEntity
import com.dox.adapter.out.persistence.entity.PatientContactJpaEntity
import com.dox.adapter.out.persistence.repository.CustomerEventJpaRepository
import com.dox.adapter.out.persistence.repository.CustomerJpaRepository
import com.dox.adapter.out.persistence.repository.CustomerNoteJpaRepository
import com.dox.adapter.out.persistence.repository.PatientContactJpaRepository
import com.dox.application.port.output.CustomerPersistencePort
import com.dox.domain.model.Customer
import com.dox.domain.model.CustomerEvent
import com.dox.domain.model.CustomerNote
import com.dox.domain.model.PatientContact
import com.dox.extensions.softDeleteById
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class CustomerPersistenceAdapter(
    private val customerJpaRepository: CustomerJpaRepository,
    private val noteJpaRepository: CustomerNoteJpaRepository,
    private val eventJpaRepository: CustomerEventJpaRepository,
    private val contactJpaRepository: PatientContactJpaRepository,
) : CustomerPersistencePort {
    override fun save(customer: Customer): Customer {
        val entity =
            customerJpaRepository.findById(customer.id).orElse(null)
                ?: CustomerJpaEntity().apply { id = customer.id }
        entity.data = customer.data
        return customerJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: UUID): Customer? = customerJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findAll(pageable: Pageable): Page<Customer> =
        customerJpaRepository.findAll(
            PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.by(Sort.Direction.DESC, "updatedAt")),
        ).map { it.toDomain() }

    override fun search(
        query: String,
        pageable: Pageable,
    ): Page<Customer> =
        customerJpaRepository.searchByNameOrCpf(
            query,
            PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.by(Sort.Direction.DESC, "updatedAt")),
        ).map { it.toDomain() }

    override fun softDelete(id: UUID) = customerJpaRepository.softDeleteById(id, "Cliente")

    override fun saveNote(note: CustomerNote): CustomerNote {
        val entity =
            CustomerNoteJpaEntity().apply {
                id = note.id
                customerId = note.customerId
                content = note.content
            }
        return noteJpaRepository.save(entity).toDomain()
    }

    override fun findNoteById(noteId: UUID): CustomerNote? = noteJpaRepository.findById(noteId).orElse(null)?.toDomain()

    override fun findNotesByCustomerId(customerId: UUID): List<CustomerNote> = noteJpaRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).map { it.toDomain() }

    override fun deleteNote(noteId: UUID) {
        noteJpaRepository.softDeleteById(noteId, "Nota")
    }

    override fun saveEvent(event: CustomerEvent): CustomerEvent {
        val entity =
            eventJpaRepository.findById(event.id).orElse(null)
                ?: CustomerEventJpaEntity().apply { id = event.id }
        entity.customerId = event.customerId
        entity.type = event.type
        entity.title = event.title
        entity.description = event.description
        entity.date = event.date
        return eventJpaRepository.save(entity).toDomain()
    }

    override fun findEventById(eventId: UUID): CustomerEvent? = eventJpaRepository.findById(eventId).orElse(null)?.toDomain()

    override fun findEventsByCustomerId(customerId: UUID): List<CustomerEvent> = eventJpaRepository.findByCustomerIdOrderByDateDesc(customerId).map { it.toDomain() }

    override fun deleteEvent(eventId: UUID) {
        eventJpaRepository.softDeleteById(eventId, "Evento")
    }

    override fun findByIds(ids: Set<UUID>): List<Customer> = customerJpaRepository.findAllById(ids).map { it.toDomain() }

    override fun findEventsByDateRange(
        from: LocalDateTime,
        to: LocalDateTime,
    ): List<CustomerEvent> = eventJpaRepository.findByDateBetweenOrderByDateAsc(from, to).map { it.toDomain() }

    override fun saveContact(contact: PatientContact): PatientContact {
        val entity =
            contactJpaRepository.findById(contact.id).orElse(null)
                ?: PatientContactJpaEntity().apply { id = contact.id }
        entity.customerId = contact.customerId
        entity.name = contact.name
        entity.relationType = contact.relationType
        entity.email = contact.email
        entity.phone = contact.phone
        entity.notes = contact.notes
        entity.canReceiveForms = contact.canReceiveForms
        return contactJpaRepository.save(entity).toDomain()
    }

    override fun findContactById(contactId: UUID): PatientContact? = contactJpaRepository.findById(contactId).orElse(null)?.toDomain()

    override fun findContactsByCustomerId(customerId: UUID): List<PatientContact> = contactJpaRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).map { it.toDomain() }

    override fun deleteContact(contactId: UUID) {
        contactJpaRepository.softDeleteById(contactId, "Contato")
    }

    private fun PatientContactJpaEntity.toDomain() =
        PatientContact(
            id = id,
            customerId = customerId,
            name = name,
            relationType = relationType,
            email = email,
            phone = phone,
            notes = notes,
            canReceiveForms = canReceiveForms,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    private fun CustomerJpaEntity.toDomain() =
        Customer(
            id = id,
            data = data,
            deleted = deleted,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    private fun CustomerNoteJpaEntity.toDomain() =
        CustomerNote(
            id = id,
            customerId = customerId,
            content = content,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    private fun CustomerEventJpaEntity.toDomain() =
        CustomerEvent(
            id = id,
            customerId = customerId,
            type = type,
            title = title,
            description = description,
            date = date,
            createdAt = createdAt,
        )
}
