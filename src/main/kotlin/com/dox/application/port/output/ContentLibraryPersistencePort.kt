package com.dox.application.port.output

import com.dox.domain.model.ContentLibraryEntry
import java.util.UUID

interface ContentLibraryPersistencePort {
    fun findAll(): List<ContentLibraryEntry>

    fun findByType(type: String): List<ContentLibraryEntry>

    fun search(query: String, type: String? = null): List<ContentLibraryEntry>

    fun findById(id: UUID): ContentLibraryEntry?

    fun save(entry: ContentLibraryEntry): ContentLibraryEntry

    fun deleteById(id: UUID)
}
