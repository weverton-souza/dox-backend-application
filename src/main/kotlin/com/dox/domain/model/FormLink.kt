package com.dox.domain.model

import com.dox.domain.enum.FormLinkStatus
import java.time.LocalDateTime
import java.util.UUID

data class FormLink(
    val id: UUID = UUID.randomUUID(),
    val formId: UUID,
    val customerId: UUID,
    val createdBy: UUID,
    val status: FormLinkStatus = FormLinkStatus.PENDING,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)
}
