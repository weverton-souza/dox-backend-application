package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.PaymentMethodCardJpaEntity
import com.dox.adapter.out.persistence.repository.PaymentMethodCardJpaRepository
import com.dox.application.port.output.PaymentMethodCardPersistencePort
import com.dox.domain.billing.PaymentMethodCard
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PaymentMethodCardPersistenceAdapter(
    private val repository: PaymentMethodCardJpaRepository,
) : PaymentMethodCardPersistencePort {
    override fun findByTenantId(tenantId: UUID): List<PaymentMethodCard> = repository.findByTenantIdOrderByCreatedAtDesc(tenantId).map { it.toDomain() }

    override fun findDefault(tenantId: UUID): PaymentMethodCard? = repository.findByTenantIdAndIsDefaultTrue(tenantId)?.toDomain()

    override fun save(card: PaymentMethodCard): PaymentMethodCard {
        val entity =
            repository.findById(card.id).orElse(null) ?: PaymentMethodCardJpaEntity(
                id = card.id,
                tenantId = card.tenantId,
                asaasCreditCardToken = card.asaasCreditCardToken,
                brand = card.brand,
                last4 = card.last4,
                holderName = card.holderName,
            )
        entity.asaasCreditCardToken = card.asaasCreditCardToken
        entity.brand = card.brand
        entity.last4 = card.last4
        entity.holderName = card.holderName
        entity.isDefault = card.isDefault
        entity.expiresAt = card.expiresAt
        return repository.save(entity).toDomain()
    }

    override fun delete(id: UUID) {
        repository.deleteById(id)
    }

    private fun PaymentMethodCardJpaEntity.toDomain() =
        PaymentMethodCard(
            id = id,
            tenantId = tenantId,
            asaasCreditCardToken = asaasCreditCardToken,
            brand = brand,
            last4 = last4,
            holderName = holderName,
            isDefault = isDefault,
            expiresAt = expiresAt,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
