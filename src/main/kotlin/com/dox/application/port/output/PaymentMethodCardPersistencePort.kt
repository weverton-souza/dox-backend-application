package com.dox.application.port.output

import com.dox.domain.billing.PaymentMethodCard
import java.util.UUID

interface PaymentMethodCardPersistencePort {
    fun findByTenantId(tenantId: UUID): List<PaymentMethodCard>

    fun findDefault(tenantId: UUID): PaymentMethodCard?

    fun save(card: PaymentMethodCard): PaymentMethodCard

    fun delete(id: UUID)
}
