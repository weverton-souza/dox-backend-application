package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.TenantModuleJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TenantModuleJpaRepository : JpaRepository<TenantModuleJpaEntity, UUID> {
    fun findByTenantId(tenantId: UUID): List<TenantModuleJpaEntity>

    fun findByTenantIdAndModuleId(
        tenantId: UUID,
        moduleId: String,
    ): TenantModuleJpaEntity?
}
