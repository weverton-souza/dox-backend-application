package com.dox.application.service

import com.dox.application.port.input.CreateReferenceEntryCommand
import com.dox.application.port.input.ReferenceEntryUseCase
import com.dox.application.port.input.UpdateReferenceEntryCommand
import com.dox.application.port.output.ReferenceEntryPersistencePort
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.ReferenceEntry
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ReferenceEntryServiceImpl(
    private val persistencePort: ReferenceEntryPersistencePort
) : ReferenceEntryUseCase {

    override fun findAll(): List<ReferenceEntry> =
        persistencePort.findAll()

    override fun search(query: String): List<ReferenceEntry> =
        persistencePort.search(query)

    override fun create(command: CreateReferenceEntryCommand): ReferenceEntry =
        persistencePort.save(
            ReferenceEntry(
                text = command.text,
                instrument = command.instrument,
                authors = command.authors,
                year = command.year
            )
        )

    override fun update(command: UpdateReferenceEntryCommand): ReferenceEntry {
        persistencePort.findById(command.id)
            ?: throw ResourceNotFoundException("Referência", command.id.toString())
        return persistencePort.save(
            ReferenceEntry(
                id = command.id,
                text = command.text,
                instrument = command.instrument,
                authors = command.authors,
                year = command.year
            )
        )
    }

    override fun delete(id: UUID) {
        persistencePort.findById(id)
            ?: throw ResourceNotFoundException("Referência", id.toString())
        persistencePort.deleteById(id)
    }
}
