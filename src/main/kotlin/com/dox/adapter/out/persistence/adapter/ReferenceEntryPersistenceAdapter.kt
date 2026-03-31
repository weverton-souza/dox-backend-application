package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.ReferenceEntryJpaEntity
import com.dox.adapter.out.persistence.repository.ReferenceEntryJpaRepository
import com.dox.application.port.output.ReferenceEntryPersistencePort
import com.dox.domain.model.ReferenceEntry
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ReferenceEntryPersistenceAdapter(
    private val repository: ReferenceEntryJpaRepository
) : ReferenceEntryPersistencePort {

    override fun findAll(): List<ReferenceEntry> =
        repository.findAllByOrderByAuthorsAscYearAsc().map { it.toDomain() }

    override fun search(query: String): List<ReferenceEntry> =
        repository.search(query).map { it.toDomain() }

    override fun findById(id: UUID): ReferenceEntry? =
        repository.findById(id).orElse(null)?.toDomain()

    override fun save(entry: ReferenceEntry): ReferenceEntry {
        val entity = ReferenceEntryJpaEntity(
            id = entry.id,
            text = entry.text,
            instrument = entry.instrument,
            authors = entry.authors,
            year = entry.year
        )
        return repository.save(entity).toDomain()
    }

    override fun deleteById(id: UUID) {
        repository.deleteById(id)
    }

    private fun ReferenceEntryJpaEntity.toDomain() = ReferenceEntry(
        id = id,
        text = text,
        instrument = instrument,
        authors = authors,
        year = year,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
