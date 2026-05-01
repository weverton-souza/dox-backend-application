package com.dox.application.port.output

import com.dox.domain.billing.ModulePrice
import java.time.LocalDateTime

interface ModulePricePersistencePort {
    fun findCurrentPrice(moduleId: String): ModulePrice?

    fun findHistory(
        moduleId: String,
        limit: Int,
    ): List<ModulePrice>

    fun save(modulePrice: ModulePrice): ModulePrice

    fun expireCurrent(
        moduleId: String,
        validUntil: LocalDateTime,
    ): Int
}
