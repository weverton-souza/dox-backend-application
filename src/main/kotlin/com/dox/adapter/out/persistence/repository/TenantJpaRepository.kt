package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.TenantJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TenantJpaRepository : JpaRepository<TenantJpaEntity, UUID> {
    fun findBySchemaName(schemaName: String): TenantJpaEntity?
}
