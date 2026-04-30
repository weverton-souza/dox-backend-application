package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.AsaasCustomerJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AsaasCustomerJpaRepository : JpaRepository<AsaasCustomerJpaEntity, UUID> {
    fun findByTenantId(tenantId: UUID): AsaasCustomerJpaEntity?

    fun findByAsaasCustomerId(asaasCustomerId: String): AsaasCustomerJpaEntity?
}
