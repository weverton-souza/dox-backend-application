package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.calendar.EventTagRequest
import com.dox.adapter.`in`.rest.dto.calendar.EventTagResponse
import com.dox.adapter.`in`.rest.resource.EventTagResource
import com.dox.application.port.input.CalendarUseCase
import com.dox.application.port.input.CreateTagCommand
import com.dox.application.port.input.UpdateTagCommand
import com.dox.domain.model.EventTag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class EventTagResourceImpl(
    private val calendarUseCase: CalendarUseCase,
) : EventTagResource {
    override fun findAll(): ResponseEntity<List<EventTagResponse>> = responseEntity(calendarUseCase.findAllTags().map { it.toResponse() })

    override fun create(request: EventTagRequest): ResponseEntity<EventTagResponse> =
        responseEntity(
            calendarUseCase.createTag(CreateTagCommand(request.name, request.color)).toResponse(),
            HttpStatus.CREATED,
        )

    override fun update(
        id: UUID,
        request: EventTagRequest,
    ): ResponseEntity<EventTagResponse> = responseEntity(calendarUseCase.updateTag(UpdateTagCommand(id, request.name, request.color)).toResponse())

    override fun delete(id: UUID): ResponseEntity<Void> {
        calendarUseCase.deleteTag(id)
        return noContent()
    }

    private fun EventTag.toResponse() = EventTagResponse(id, name, color, createdAt, updatedAt)
}
