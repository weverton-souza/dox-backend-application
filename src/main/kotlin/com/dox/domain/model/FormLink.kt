package com.dox.domain.model

import com.dox.domain.enum.FormLinkStatus
import com.dox.domain.enum.RespondentType
import com.dox.extensions.isExpired
import java.time.LocalDateTime
import java.util.UUID

data class FormLink(
    val id: UUID = UUID.randomUUID(),
    val formId: UUID,
    val formVersionId: UUID,
    val customerId: UUID,
    val customerContactId: UUID? = null,
    val respondentType: RespondentType = RespondentType.CUSTOMER,
    val createdBy: UUID,
    val status: FormLinkStatus = FormLinkStatus.PENDING,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
) {
    fun isExpired(): Boolean = expiresAt.isExpired()
}
