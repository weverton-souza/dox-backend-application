package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.TenantJpaEntity
import com.dox.adapter.out.persistence.repository.TenantJpaRepository
import com.dox.application.port.output.TenantPersistencePort
import com.dox.domain.model.Tenant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TenantPersistenceAdapter(
    private val tenantJpaRepository: TenantJpaRepository,
) : TenantPersistencePort {
    override fun save(tenant: Tenant): Tenant {
        val entity =
            TenantJpaEntity().apply {
                id = tenant.id
                schemaName = tenant.schemaName
                type = tenant.type
                name = tenant.name
                vertical = tenant.vertical
            }
        return tenantJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: UUID): Tenant? = tenantJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findBySchemaName(schemaName: String): Tenant? = tenantJpaRepository.findBySchemaName(schemaName)?.toDomain()

    override fun findAllPaginated(
        search: String?,
        pageable: Pageable,
    ): Page<Tenant> {
        val page =
            if (search.isNullOrBlank()) {
                tenantJpaRepository.findAll(pageable)
            } else {
                tenantJpaRepository.findByNameContainingIgnoreCase(search, pageable)
            }
        return page.map { it.toDomain() }
    }

    private fun TenantJpaEntity.toDomain() =
        Tenant(
            id = id,
            schemaName = schemaName,
            type = type,
            name = name,
            vertical = vertical,
            createdAt = createdAt,
        )
}
