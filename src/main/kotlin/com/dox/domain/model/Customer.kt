package com.dox.domain.model

import com.dox.domain.enum.CustomerContactRelationType
import java.time.LocalDateTime
import java.util.UUID

data class Customer(
    val id: UUID = UUID.randomUUID(),
    val data: Map<String, Any?> = emptyMap(),
    val deleted: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
) {
    fun displayName(): String? = data["name"] as? String
}

data class CustomerContact(
    val id: UUID = UUID.randomUUID(),
    val customerId: UUID,
    val name: String,
    val relationType: CustomerContactRelationType,
    val email: String? = null,
    val phone: String? = null,
    val notes: String? = null,
    val canReceiveForms: Boolean = true,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)

data class CustomerNote(
    val id: UUID = UUID.randomUUID(),
    val customerId: UUID,
    val content: String,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)

data class CustomerEvent(
    val id: UUID = UUID.randomUUID(),
    val customerId: UUID,
    val type: String,
    val title: String,
    val description: String? = null,
    val date: LocalDateTime,
    val createdAt: LocalDateTime? = null,
)
