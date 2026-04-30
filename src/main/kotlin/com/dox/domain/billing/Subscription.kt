package com.dox.domain.billing

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Subscription(
    val id: UUID = UUID.randomUUID(),
    val tenantId: UUID,
    val asaasSubscriptionId: String? = null,
    val status: SubscriptionStatus,
    val billingCycle: BillingCycle,
    val billingType: BillingType,
    val valueCents: Int,
    val currentPeriodStart: LocalDateTime? = null,
    val currentPeriodEnd: LocalDateTime? = null,
    val nextDueDate: LocalDate? = null,
    val trialEnd: LocalDateTime? = null,
    val canceledAt: LocalDateTime? = null,
    val cancelEffectiveAt: LocalDateTime? = null,
    val cancelReason: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
