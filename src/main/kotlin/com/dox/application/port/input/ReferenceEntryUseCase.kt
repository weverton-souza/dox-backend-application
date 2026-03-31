package com.dox.application.port.input

import com.dox.domain.model.ReferenceEntry
import java.util.UUID

data class CreateReferenceEntryCommand(
    val text: String,
    val instrument: String? = null,
    val authors: String? = null,
    val year: Int? = null
)

data class UpdateReferenceEntryCommand(
    val id: UUID,
    val text: String,
    val instrument: String? = null,
    val authors: String? = null,
    val year: Int? = null
)

interface ReferenceEntryUseCase {
    fun findAll(): List<ReferenceEntry>
    fun search(query: String): List<ReferenceEntry>
    fun create(command: CreateReferenceEntryCommand): ReferenceEntry
    fun update(command: UpdateReferenceEntryCommand): ReferenceEntry
    fun delete(id: UUID)
}
