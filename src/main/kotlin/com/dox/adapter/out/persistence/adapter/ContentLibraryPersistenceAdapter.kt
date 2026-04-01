package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.ContentLibraryJpaEntity
import com.dox.adapter.out.persistence.repository.ContentLibraryJpaRepository
import com.dox.application.port.output.ContentLibraryPersistencePort
import com.dox.domain.model.ContentLibraryEntry
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ContentLibraryPersistenceAdapter(
    private val repository: ContentLibraryJpaRepository,
) : ContentLibraryPersistencePort {
    override fun findAll(): List<ContentLibraryEntry> = repository.findAllByOrderByTypeAscTitleAsc().map { it.toDomain() }

    override fun findByType(type: String): List<ContentLibraryEntry> = repository.findByTypeOrderByTitleAsc(type).map { it.toDomain() }

    override fun search(
        query: String,
        type: String?,
    ): List<ContentLibraryEntry> = repository.search(query, type).map { it.toDomain() }

    override fun findById(id: UUID): ContentLibraryEntry? = repository.findById(id).orElse(null)?.toDomain()

    override fun save(entry: ContentLibraryEntry): ContentLibraryEntry {
        val entity =
            ContentLibraryJpaEntity(
                id = entry.id, title = entry.title, content = entry.content.toMutableList(),
                type = entry.type, category = entry.category, instrument = entry.instrument,
                authors = entry.authors, year = entry.year, tags = entry.tags,
            )
        return repository.save(entity).toDomain()
    }

    override fun deleteById(id: UUID) = repository.deleteById(id)

    private fun ContentLibraryJpaEntity.toDomain() =
        ContentLibraryEntry(
            id = id, title = title, content = content, type = type, category = category,
            instrument = instrument, authors = authors, year = year, tags = tags,
            createdAt = createdAt, updatedAt = updatedAt,
        )
}
