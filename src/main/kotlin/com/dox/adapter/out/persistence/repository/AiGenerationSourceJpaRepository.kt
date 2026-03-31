package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.AiGenerationSourceJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AiGenerationSourceJpaRepository : JpaRepository<AiGenerationSourceJpaEntity, UUID> {
    fun findByReportId(reportId: UUID): List<AiGenerationSourceJpaEntity>

    fun findByGenerationId(generationId: UUID): List<AiGenerationSourceJpaEntity>
}
