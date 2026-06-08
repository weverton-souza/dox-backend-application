package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.OrganizationJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface OrganizationJpaRepository : JpaRepository<OrganizationJpaEntity, UUID> {
    @Query("SELECT o FROM OrganizationJpaEntity o WHERE o.tenant.id = :tenantId")
    fun findByTenantId(
        @Param("tenantId") tenantId: UUID,
    ): OrganizationJpaEntity?
}
