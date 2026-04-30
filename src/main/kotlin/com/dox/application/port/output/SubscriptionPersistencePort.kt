package com.dox.application.port.output

import com.dox.domain.billing.Subscription
import java.util.UUID

interface SubscriptionPersistencePort {
    fun findByTenantId(tenantId: UUID): Subscription?

    fun findByAsaasSubscriptionId(asaasSubscriptionId: String): Subscription?

    fun save(subscription: Subscription): Subscription
}
