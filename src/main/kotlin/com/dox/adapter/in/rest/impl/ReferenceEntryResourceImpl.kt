package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.reference.ReferenceEntryRequest
import com.dox.adapter.`in`.rest.dto.reference.ReferenceEntryResponse
import com.dox.adapter.`in`.rest.resource.ReferenceEntryResource
import com.dox.application.port.input.CreateReferenceEntryCommand
import com.dox.application.port.input.ReferenceEntryUseCase
import com.dox.application.port.input.UpdateReferenceEntryCommand
import com.dox.domain.model.ReferenceEntry
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class ReferenceEntryResourceImpl(
    private val referenceEntryUseCase: ReferenceEntryUseCase
) : ReferenceEntryResource {

    override fun findAll(query: String?): ResponseEntity<List<ReferenceEntryResponse>> {
        val entries = if (!query.isNullOrBlank()) {
            referenceEntryUseCase.search(query)
        } else {
            referenceEntryUseCase.findAll()
        }
        return responseEntity(entries.map { it.toResponse() })
    }

    override fun create(request: ReferenceEntryRequest): ResponseEntity<ReferenceEntryResponse> =
        responseEntity(
            referenceEntryUseCase.create(
                CreateReferenceEntryCommand(request.text, request.instrument, request.authors, request.year)
            ).toResponse(),
            HttpStatus.CREATED
        )

    override fun update(id: UUID, request: ReferenceEntryRequest): ResponseEntity<ReferenceEntryResponse> =
        responseEntity(
            referenceEntryUseCase.update(
                UpdateReferenceEntryCommand(id, request.text, request.instrument, request.authors, request.year)
            ).toResponse()
        )

    override fun delete(id: UUID): ResponseEntity<Void> {
        referenceEntryUseCase.delete(id)
        return noContent()
    }

    private fun ReferenceEntry.toResponse() = ReferenceEntryResponse(
        id = id, text = text, instrument = instrument,
        authors = authors, year = year, createdAt = createdAt, updatedAt = updatedAt
    )
}
