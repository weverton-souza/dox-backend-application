package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.AsaasCustomerJpaEntity
import com.dox.adapter.out.persistence.repository.AsaasCustomerJpaRepository
import com.dox.application.port.output.AsaasCustomerPersistencePort
import com.dox.domain.billing.AsaasCustomer
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AsaasCustomerPersistenceAdapter(
    private val repository: AsaasCustomerJpaRepository,
) : AsaasCustomerPersistencePort {
    override fun findByTenantId(tenantId: UUID): AsaasCustomer? = repository.findByTenantId(tenantId)?.toDomain()

    override fun save(customer: AsaasCustomer): AsaasCustomer {
        val entity =
            repository.findByTenantId(customer.tenantId)?.apply {
                asaasCustomerId = customer.asaasCustomerId
                cpfCnpj = customer.cpfCnpj
                email = customer.email
                name = customer.name
            } ?: AsaasCustomerJpaEntity(
                id = customer.id,
                tenantId = customer.tenantId,
                asaasCustomerId = customer.asaasCustomerId,
                cpfCnpj = customer.cpfCnpj,
                email = customer.email,
                name = customer.name,
            )
        return repository.save(entity).toDomain()
    }

    private fun AsaasCustomerJpaEntity.toDomain() =
        AsaasCustomer(
            id = id,
            tenantId = tenantId,
            asaasCustomerId = asaasCustomerId,
            cpfCnpj = cpfCnpj,
            email = email,
            name = name,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
