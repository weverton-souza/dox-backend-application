package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.CustomerEventJpaEntity
import com.dox.adapter.out.persistence.entity.CustomerJpaEntity
import com.dox.adapter.out.persistence.entity.CustomerNoteJpaEntity
import com.dox.adapter.out.persistence.repository.CustomerEventJpaRepository
import com.dox.adapter.out.persistence.repository.CustomerJpaRepository
import com.dox.adapter.out.persistence.repository.CustomerNoteJpaRepository
import com.dox.application.port.output.CustomerPersistencePort
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.Customer
import com.dox.domain.model.CustomerEvent
import com.dox.domain.model.CustomerNote
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CustomerPersistenceAdapter(
    private val customerJpaRepository: CustomerJpaRepository,
    private val noteJpaRepository: CustomerNoteJpaRepository,
    private val eventJpaRepository: CustomerEventJpaRepository
) : CustomerPersistencePort {

    override fun save(customer: Customer): Customer {
        val entity = customerJpaRepository.findById(customer.id).orElse(null)
            ?: CustomerJpaEntity().apply { id = customer.id }
        entity.data = customer.data
        return customerJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: UUID): Customer? =
        customerJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findAll(pageable: Pageable): Page<Customer> =
        customerJpaRepository.findAll(pageable).map { it.toDomain() }

    override fun search(query: String, pageable: Pageable): Page<Customer> =
        customerJpaRepository.searchByNameOrCpf(query, pageable).map { it.toDomain() }

    override fun softDelete(id: UUID) {
        val entity = customerJpaRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Cliente", id.toString()) }
        entity.deleted = true
        customerJpaRepository.save(entity)
    }

    override fun saveNote(note: CustomerNote): CustomerNote {
        val entity = CustomerNoteJpaEntity().apply {
            id = note.id
            customerId = note.customerId
            content = note.content
        }
        return noteJpaRepository.save(entity).toDomain()
    }

    override fun findNotesByCustomerId(customerId: UUID): List<CustomerNote> =
        noteJpaRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).map { it.toDomain() }

    override fun deleteNote(noteId: UUID) {
        noteJpaRepository.deleteById(noteId)
    }

    override fun saveEvent(event: CustomerEvent): CustomerEvent {
        val entity = CustomerEventJpaEntity().apply {
            id = event.id
            customerId = event.customerId
            type = event.type
            title = event.title
            description = event.description
            date = event.date
        }
        return eventJpaRepository.save(entity).toDomain()
    }

    override fun findEventsByCustomerId(customerId: UUID): List<CustomerEvent> =
        eventJpaRepository.findByCustomerIdOrderByDateDesc(customerId).map { it.toDomain() }

    override fun deleteEvent(eventId: UUID) {
        eventJpaRepository.deleteById(eventId)
    }

    private fun CustomerJpaEntity.toDomain() = Customer(
        id = id,
        data = data,
        deleted = deleted,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun CustomerNoteJpaEntity.toDomain() = CustomerNote(
        id = id,
        customerId = customerId,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun CustomerEventJpaEntity.toDomain() = CustomerEvent(
        id = id,
        customerId = customerId,
        type = type,
        title = title,
        description = description,
        date = date,
        createdAt = createdAt
    )
}
