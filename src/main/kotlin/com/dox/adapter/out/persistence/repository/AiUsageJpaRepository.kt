package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.AiUsageJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.UUID

interface AiUsageJpaRepository : JpaRepository<AiUsageJpaEntity, UUID> {

    fun findByReportId(reportId: UUID): List<AiUsageJpaEntity>

    @Query(
        "SELECT COUNT(e) FROM AiUsageJpaEntity e " +
                "WHERE e.professionalId = :professionalId " +
                "AND e.createdAt >= :startDate " +
                "AND e.createdAt < :endDate " +
                "AND e.status = com.dox.domain.enum.AiGenerationStatus.SUCCESS"
    )
    fun countSuccessByProfessionalAndPeriod(
        professionalId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Int

    fun countByReportIdAndStatusNot(
        reportId: UUID,
        status: com.dox.domain.enum.AiGenerationStatus
    ): Int

    @Query(
        "SELECT e FROM AiUsageJpaEntity e " +
                "WHERE e.professionalId = :professionalId " +
                "AND e.createdAt >= :startDate " +
                "AND e.createdAt < :endDate " +
                "ORDER BY e.createdAt DESC"
    )
    fun findByProfessionalAndPeriod(
        professionalId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<AiUsageJpaEntity>
}
