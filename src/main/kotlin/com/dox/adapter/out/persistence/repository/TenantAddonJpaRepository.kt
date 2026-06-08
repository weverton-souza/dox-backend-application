package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.TenantAddonJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TenantAddonJpaRepository : JpaRepository<TenantAddonJpaEntity, UUID> {
    fun findByTenantIdAndAddonIdAndCanceledAtIsNull(
        tenantId: UUID,
        addonId: String,
    ): TenantAddonJpaEntity?

    fun findByTenantIdAndAddonId(
        tenantId: UUID,
        addonId: String,
    ): TenantAddonJpaEntity?
}
