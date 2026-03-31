package com.dox.application.service

import com.dox.application.port.input.ContentLibraryUseCase
import com.dox.application.port.input.CreateContentLibraryCommand
import com.dox.application.port.input.UpdateContentLibraryCommand
import com.dox.application.port.output.ContentLibraryPersistencePort
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.ContentLibraryEntry
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ContentLibraryServiceImpl(
    private val persistencePort: ContentLibraryPersistencePort
) : ContentLibraryUseCase {
    override fun findAll() = persistencePort.findAll()

    override fun findByType(type: String) = persistencePort.findByType(type)

    override fun search(query: String, type: String?) = persistencePort.search(query, type)

    override fun create(command: CreateContentLibraryCommand): ContentLibraryEntry =
        persistencePort.save(
            ContentLibraryEntry(
                title = command.title,
                content = command.content,
                type = command.type,
                category = command.category,
                instrument = command.instrument,
                authors = command.authors,
                year = command.year,
                tags = command.tags
            )
        )

    override fun update(command: UpdateContentLibraryCommand): ContentLibraryEntry {
        persistencePort.findById(command.id) ?: throw ResourceNotFoundException("Conteúdo", command.id.toString())
        return persistencePort.save(
            ContentLibraryEntry(
                id = command.id, title = command.title, content = command.content, type = command.type,
                category = command.category, instrument = command.instrument,
                authors = command.authors, year = command.year, tags = command.tags
            )
        )
    }

    override fun delete(id: UUID) {
        persistencePort.findById(id) ?: throw ResourceNotFoundException("Conteúdo", id.toString())
        persistencePort.deleteById(id)
    }
}
