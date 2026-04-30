package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.SubscriptionJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SubscriptionJpaRepository : JpaRepository<SubscriptionJpaEntity, UUID> {
    fun findByTenantId(tenantId: UUID): SubscriptionJpaEntity?

    fun findByAsaasSubscriptionId(asaasSubscriptionId: String): SubscriptionJpaEntity?
}
