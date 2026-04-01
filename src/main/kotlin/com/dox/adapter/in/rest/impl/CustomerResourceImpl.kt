package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.customer.CustomerEventRequest
import com.dox.adapter.`in`.rest.dto.customer.CustomerEventResponse
import com.dox.adapter.`in`.rest.dto.customer.CustomerNoteRequest
import com.dox.adapter.`in`.rest.dto.customer.CustomerNoteResponse
import com.dox.adapter.`in`.rest.dto.customer.CustomerRequest
import com.dox.adapter.`in`.rest.dto.customer.CustomerResponse
import com.dox.adapter.`in`.rest.resource.CustomerResource
import com.dox.application.port.input.CreateCustomerCommand
import com.dox.application.port.input.CreateCustomerEventCommand
import com.dox.application.port.input.CreateCustomerNoteCommand
import com.dox.application.port.input.CustomerUseCase
import com.dox.application.port.input.UpdateCustomerCommand
import com.dox.application.port.input.UpdateCustomerEventCommand
import com.dox.domain.model.Customer
import com.dox.domain.model.CustomerEvent
import com.dox.domain.model.CustomerNote
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class CustomerResourceImpl(
    private val customerUseCase: CustomerUseCase,
) : CustomerResource {
    override fun findAll(parameters: Map<String, Any>): ResponseEntity<Page<CustomerResponse>> {
        val search = parameters["search"]?.toString()?.takeIf { it.isNotBlank() }
        val pageable = retrievePageableParameter(parameters)
        return responseEntity(customerUseCase.findAll(search, pageable).map { it.toResponse() })
    }

    override fun create(request: CustomerRequest): ResponseEntity<CustomerResponse> = responseEntity(customerUseCase.create(CreateCustomerCommand(request.data)).toResponse(), HttpStatus.CREATED)

    override fun findById(id: UUID): ResponseEntity<CustomerResponse> = responseEntity(customerUseCase.findById(id).toResponse())

    override fun update(
        id: UUID,
        request: CustomerRequest,
    ): ResponseEntity<CustomerResponse> = responseEntity(customerUseCase.update(UpdateCustomerCommand(id, request.data)).toResponse())

    override fun delete(id: UUID): ResponseEntity<Void> {
        customerUseCase.delete(id)
        return noContent()
    }

    override fun getNotes(id: UUID): ResponseEntity<List<CustomerNoteResponse>> = responseEntity(customerUseCase.getNotes(id).map { it.toResponse() })

    override fun addNote(
        id: UUID,
        request: CustomerNoteRequest,
    ): ResponseEntity<CustomerNoteResponse> =
        responseEntity(
            customerUseCase.addNote(CreateCustomerNoteCommand(id, request.content)).toResponse(),
            HttpStatus.CREATED,
        )

    override fun deleteNote(
        id: UUID,
        noteId: UUID,
    ): ResponseEntity<Void> {
        customerUseCase.deleteNote(noteId)
        return noContent()
    }

    override fun getEvents(id: UUID): ResponseEntity<List<CustomerEventResponse>> = responseEntity(customerUseCase.getEvents(id).map { it.toResponse() })

    override fun addEvent(
        id: UUID,
        request: CustomerEventRequest,
    ): ResponseEntity<CustomerEventResponse> =
        responseEntity(
            customerUseCase.addEvent(
                CreateCustomerEventCommand(id, request.type, request.title, request.description, request.date),
            ).toResponse(),
            HttpStatus.CREATED,
        )

    override fun updateEvent(
        id: UUID,
        eventId: UUID,
        request: CustomerEventRequest,
    ): ResponseEntity<CustomerEventResponse> =
        responseEntity(
            customerUseCase.updateEvent(
                UpdateCustomerEventCommand(eventId, id, request.type, request.title, request.description, request.date),
            ).toResponse(),
        )

    override fun deleteEvent(
        id: UUID,
        eventId: UUID,
    ): ResponseEntity<Void> {
        customerUseCase.deleteEvent(eventId)
        return noContent()
    }

    private fun Customer.toResponse() = CustomerResponse(id, data, createdAt, updatedAt)

    private fun CustomerNote.toResponse() = CustomerNoteResponse(id, customerId, content, createdAt, updatedAt)

    private fun CustomerEvent.toResponse() = CustomerEventResponse(id, customerId, type, title, description, date, createdAt)
}
