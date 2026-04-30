package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.SubscriptionJpaEntity
import com.dox.adapter.out.persistence.repository.SubscriptionJpaRepository
import com.dox.application.port.output.SubscriptionPersistencePort
import com.dox.domain.billing.BillingCycle
import com.dox.domain.billing.BillingType
import com.dox.domain.billing.Subscription
import com.dox.domain.billing.SubscriptionStatus
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SubscriptionPersistenceAdapter(
    private val repository: SubscriptionJpaRepository,
) : SubscriptionPersistencePort {
    override fun findByTenantId(tenantId: UUID): Subscription? = repository.findByTenantId(tenantId)?.toDomain()

    override fun findByAsaasSubscriptionId(asaasSubscriptionId: String): Subscription? = repository.findByAsaasSubscriptionId(asaasSubscriptionId)?.toDomain()

    override fun save(subscription: Subscription): Subscription {
        val entity =
            repository.findByTenantId(subscription.tenantId) ?: SubscriptionJpaEntity(
                id = subscription.id,
                tenantId = subscription.tenantId,
                status = subscription.status.name,
                billingCycle = subscription.billingCycle.name,
                billingType = subscription.billingType.name,
                valueCents = subscription.valueCents,
            )
        entity.asaasSubscriptionId = subscription.asaasSubscriptionId
        entity.status = subscription.status.name
        entity.billingCycle = subscription.billingCycle.name
        entity.billingType = subscription.billingType.name
        entity.valueCents = subscription.valueCents
        entity.currentPeriodStart = subscription.currentPeriodStart
        entity.currentPeriodEnd = subscription.currentPeriodEnd
        entity.nextDueDate = subscription.nextDueDate
        entity.trialEnd = subscription.trialEnd
        entity.canceledAt = subscription.canceledAt
        entity.cancelEffectiveAt = subscription.cancelEffectiveAt
        entity.cancelReason = subscription.cancelReason
        return repository.save(entity).toDomain()
    }

    private fun SubscriptionJpaEntity.toDomain() =
        Subscription(
            id = id,
            tenantId = tenantId,
            asaasSubscriptionId = asaasSubscriptionId,
            status = SubscriptionStatus.valueOf(status),
            billingCycle = BillingCycle.valueOf(billingCycle),
            billingType = BillingType.valueOf(billingType),
            valueCents = valueCents,
            currentPeriodStart = currentPeriodStart,
            currentPeriodEnd = currentPeriodEnd,
            nextDueDate = nextDueDate,
            trialEnd = trialEnd,
            canceledAt = canceledAt,
            cancelEffectiveAt = cancelEffectiveAt,
            cancelReason = cancelReason,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
