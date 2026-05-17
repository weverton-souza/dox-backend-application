package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.PaymentMethodCardJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PaymentMethodCardJpaRepository : JpaRepository<PaymentMethodCardJpaEntity, UUID> {
    fun findByTenantIdOrderByDisplayOrderAsc(tenantId: UUID): List<PaymentMethodCardJpaEntity>

    fun findByTenantIdAndIsDefaultTrue(tenantId: UUID): PaymentMethodCardJpaEntity?

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE PaymentMethodCardJpaEntity c SET c.isDefault = false WHERE c.tenantId = :tenantId AND c.isDefault = true")
    fun clearDefaultForTenant(
        @Param("tenantId") tenantId: UUID,
    ): Int
}
