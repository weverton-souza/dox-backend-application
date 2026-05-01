package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.ModulePriceJpaEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.UUID

interface ModulePriceJpaRepository : JpaRepository<ModulePriceJpaEntity, UUID> {
    fun findFirstByModuleIdAndValidUntilIsNull(moduleId: String): ModulePriceJpaEntity?

    fun findByModuleIdOrderByValidFromDesc(
        moduleId: String,
        pageable: Pageable,
    ): List<ModulePriceJpaEntity>

    @Modifying
    @Query(
        "UPDATE ModulePriceJpaEntity m SET m.validUntil = :validUntil " +
            "WHERE m.moduleId = :moduleId AND m.validUntil IS NULL",
    )
    fun expireCurrent(
        @Param("moduleId") moduleId: String,
        @Param("validUntil") validUntil: LocalDateTime,
    ): Int
}
