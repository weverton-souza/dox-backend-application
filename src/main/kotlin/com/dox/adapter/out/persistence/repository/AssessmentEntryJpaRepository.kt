package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.AssessmentEntryJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AssessmentEntryJpaRepository : JpaRepository<AssessmentEntryJpaEntity, UUID> {
    fun findByAssessmentIdOrderByOrderIndexAsc(assessmentId: UUID): List<AssessmentEntryJpaEntity>

    fun findByAssessmentIdIn(assessmentIds: Collection<UUID>): List<AssessmentEntryJpaEntity>

    fun deleteByAssessmentId(assessmentId: UUID)
}
