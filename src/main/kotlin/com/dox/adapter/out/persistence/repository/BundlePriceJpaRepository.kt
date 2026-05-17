package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.BundlePriceJpaEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.UUID

interface BundlePriceJpaRepository : JpaRepository<BundlePriceJpaEntity, UUID> {
    fun findFirstByBundleIdAndValidUntilIsNull(bundleId: String): BundlePriceJpaEntity?

    fun findByBundleIdOrderByValidFromDesc(
        bundleId: String,
        pageable: Pageable,
    ): List<BundlePriceJpaEntity>

    @Modifying
    @Query(
        "UPDATE BundlePriceJpaEntity b SET b.validUntil = :validUntil " +
            "WHERE b.bundleId = :bundleId AND b.validUntil IS NULL",
    )
    fun expireCurrent(
        @Param("bundleId") bundleId: String,
        @Param("validUntil") validUntil: LocalDateTime,
    ): Int
}
