package com.dox.domain.billing

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class PaymentMethodCard(
    val id: UUID = UUID.randomUUID(),
    val tenantId: UUID,
    val asaasCreditCardToken: String,
    val brand: String,
    val last4: String,
    val holderName: String,
    val isDefault: Boolean = false,
    val expiresAt: LocalDate? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
