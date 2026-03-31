package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.contentlibrary.ContentLibraryRequest
import com.dox.adapter.`in`.rest.dto.contentlibrary.ContentLibraryResponse
import com.dox.adapter.`in`.rest.resource.ContentLibraryResource
import com.dox.application.port.input.ContentLibraryUseCase
import com.dox.application.port.input.CreateContentLibraryCommand
import com.dox.application.port.input.UpdateContentLibraryCommand
import com.dox.domain.model.ContentLibraryEntry
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class ContentLibraryResourceImpl(
    private val contentLibraryUseCase: ContentLibraryUseCase
) : ContentLibraryResource {
    override fun findAll(query: String?, type: String?): ResponseEntity<List<ContentLibraryResponse>> {
        val entries = when {
            !query.isNullOrBlank() -> contentLibraryUseCase.search(query, type)
            !type.isNullOrBlank() -> contentLibraryUseCase.findByType(type)
            else -> contentLibraryUseCase.findAll()
        }
        return responseEntity(entries.map { it.toResponse() })
    }

    override fun create(request: ContentLibraryRequest): ResponseEntity<ContentLibraryResponse> =
        responseEntity(
            contentLibraryUseCase.create(
                CreateContentLibraryCommand(
                    title = request.title,
                    content = request.content,
                    type = request.type,
                    category = request.category,
                    instrument = request.instrument,
                    authors = request.authors,
                    year = request.year,
                    tags = request.tags
                )
            ).toResponse(),
            HttpStatus.CREATED
        )

    override fun update(id: UUID, request: ContentLibraryRequest): ResponseEntity<ContentLibraryResponse> =
        responseEntity(
            contentLibraryUseCase.update(
                UpdateContentLibraryCommand(
                    id = id, title = request.title, content = request.content, type = request.type,
                    category = request.category, instrument = request.instrument,
                    authors = request.authors, year = request.year, tags = request.tags
                )
            ).toResponse()
        )

    override fun delete(id: UUID): ResponseEntity<Void> {
        contentLibraryUseCase.delete(id)
        return noContent()
    }

    private fun ContentLibraryEntry.toResponse() = ContentLibraryResponse(
        id = id, title = title, content = content, type = type, category = category,
        instrument = instrument, authors = authors, year = year, tags = tags,
        createdAt = createdAt, updatedAt = updatedAt
    )
}
