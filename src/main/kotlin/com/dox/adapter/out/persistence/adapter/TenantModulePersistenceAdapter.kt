package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.TenantModuleJpaEntity
import com.dox.adapter.out.persistence.repository.TenantModuleJpaRepository
import com.dox.application.port.output.TenantModulePersistencePort
import com.dox.domain.billing.ModuleSource
import com.dox.domain.billing.ModuleStatus
import com.dox.domain.billing.TenantModule
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TenantModulePersistenceAdapter(
    private val repository: TenantModuleJpaRepository,
) : TenantModulePersistencePort {
    override fun findByTenantId(tenantId: UUID): List<TenantModule> = repository.findByTenantId(tenantId).map { it.toDomain() }

    override fun findByTenantIdAndModuleId(
        tenantId: UUID,
        moduleId: String,
    ): TenantModule? = repository.findByTenantIdAndModuleId(tenantId, moduleId)?.toDomain()

    override fun save(tenantModule: TenantModule): TenantModule {
        val entity =
            repository.findByTenantIdAndModuleId(tenantModule.tenantId, tenantModule.moduleId)
                ?: TenantModuleJpaEntity(
                    id = tenantModule.id,
                    tenantId = tenantModule.tenantId,
                    moduleId = tenantModule.moduleId,
                    status = tenantModule.status.name,
                    source = tenantModule.source.name,
                )
        entity.status = tenantModule.status.name
        entity.source = tenantModule.source.name
        entity.sourceId = tenantModule.sourceId
        entity.activatedAt = tenantModule.activatedAt
        entity.expiresAt = tenantModule.expiresAt
        entity.graceUntil = tenantModule.graceUntil
        entity.basePriceCents = tenantModule.basePriceCents
        entity.finalPriceCents = tenantModule.finalPriceCents
        entity.priceLocked = tenantModule.priceLocked
        entity.priceLockedAt = tenantModule.priceLockedAt
        entity.canceledAt = tenantModule.canceledAt
        entity.cancelReason = tenantModule.cancelReason
        return repository.save(entity).toDomain()
    }

    override fun delete(id: UUID) {
        repository.deleteById(id)
    }

    private fun TenantModuleJpaEntity.toDomain() =
        TenantModule(
            id = id,
            tenantId = tenantId,
            moduleId = moduleId,
            status = ModuleStatus.valueOf(status),
            source = ModuleSource.valueOf(source),
            sourceId = sourceId,
            activatedAt = activatedAt,
            expiresAt = expiresAt,
            graceUntil = graceUntil,
            basePriceCents = basePriceCents,
            finalPriceCents = finalPriceCents,
            priceLocked = priceLocked,
            priceLockedAt = priceLockedAt,
            canceledAt = canceledAt,
            cancelReason = cancelReason,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
