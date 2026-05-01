package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.PromotionJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.UUID

interface PromotionJpaRepository : JpaRepository<PromotionJpaEntity, UUID> {
    fun findByCode(code: String): PromotionJpaEntity?

    fun findAllByArchivedAtIsNull(): List<PromotionJpaEntity>

    fun findAllByArchivedAtIsNull(pageable: Pageable): Page<PromotionJpaEntity>

    @Modifying
    @Query(
        "UPDATE PromotionJpaEntity p SET p.currentRedemptions = p.currentRedemptions + 1 " +
            "WHERE p.id = :promotionId " +
            "AND p.archivedAt IS NULL " +
            "AND (p.maxRedemptions IS NULL OR p.currentRedemptions < p.maxRedemptions) " +
            "AND (p.validFrom IS NULL OR p.validFrom <= :now) " +
            "AND (p.validUntil IS NULL OR p.validUntil > :now)",
    )
    fun tryIncrementRedemption(
        @Param("promotionId") promotionId: UUID,
        @Param("now") now: LocalDateTime,
    ): Int
}
