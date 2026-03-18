package com.dox.adapter.`in`.rest.dto.customer

import java.time.LocalDateTime
import java.util.UUID

data class CustomerRequest(
    val data: Map<String, Any?>
)

data class CustomerResponse(
    val id: UUID,
    val data: Map<String, Any?>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class CustomerNoteRequest(
    val content: String
)

data class CustomerNoteResponse(
    val id: UUID,
    val customerId: UUID,
    val content: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class CustomerEventRequest(
    val type: String,
    val title: String,
    val description: String? = null,
    val date: LocalDateTime
)

data class CustomerEventResponse(
    val id: UUID,
    val customerId: UUID,
    val type: String,
    val title: String,
    val description: String?,
    val date: LocalDateTime,
    val createdAt: LocalDateTime?
)

data class CustomerCalendarEventResponse(
    val id: UUID,
    val customerId: UUID,
    val customerName: String,
    val type: String,
    val title: String,
    val description: String?,
    val date: LocalDateTime,
    val createdAt: LocalDateTime?
)
