package com.dox.application.port.output

import com.dox.domain.billing.Payment
import java.time.LocalDate
import java.util.UUID

interface PaymentPersistencePort {
    fun findByAsaasPaymentId(asaasPaymentId: String): Payment?

    fun findByTenantId(tenantId: UUID): List<Payment>

    fun findByTenantIdAndDueDateBetween(
        tenantId: UUID,
        from: LocalDate,
        to: LocalDate,
    ): List<Payment>

    fun save(payment: Payment): Payment
}
