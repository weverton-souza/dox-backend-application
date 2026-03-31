package com.dox.application.port.input

import com.dox.domain.model.ContentLibraryEntry
import java.util.UUID

data class CreateContentLibraryCommand(
    val title: String,
    val content: List<Map<String, Any?>>,
    val type: String = "reference",
    val category: String = "general",
    val instrument: String? = null,
    val authors: String? = null,
    val year: Int? = null,
    val tags: String? = null
)

data class UpdateContentLibraryCommand(
    val id: UUID,
    val title: String,
    val content: List<Map<String, Any?>>,
    val type: String = "reference",
    val category: String = "general",
    val instrument: String? = null,
    val authors: String? = null,
    val year: Int? = null,
    val tags: String? = null
)

interface ContentLibraryUseCase {
    fun findAll(): List<ContentLibraryEntry>

    fun findByType(type: String): List<ContentLibraryEntry>

    fun search(query: String, type: String? = null): List<ContentLibraryEntry>

    fun create(command: CreateContentLibraryCommand): ContentLibraryEntry

    fun update(command: UpdateContentLibraryCommand): ContentLibraryEntry

    fun delete(id: UUID)
}
