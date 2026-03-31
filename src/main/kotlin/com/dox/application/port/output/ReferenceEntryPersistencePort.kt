package com.dox.application.port.output

import com.dox.domain.model.ReferenceEntry
import java.util.UUID

interface ReferenceEntryPersistencePort {
    fun findAll(): List<ReferenceEntry>
    fun search(query: String): List<ReferenceEntry>
    fun findById(id: UUID): ReferenceEntry?
    fun save(entry: ReferenceEntry): ReferenceEntry
    fun deleteById(id: UUID)
}
