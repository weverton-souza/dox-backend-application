package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.ReportVersionJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ReportVersionJpaRepository : JpaRepository<ReportVersionJpaEntity, UUID> {
    fun findByReportIdOrderByCreatedAtDesc(reportId: UUID): List<ReportVersionJpaEntity>

    fun countByReportId(reportId: UUID): Long
}
