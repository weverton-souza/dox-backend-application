package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.AssessmentJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AssessmentJpaRepository : JpaRepository<AssessmentJpaEntity, UUID> {
    fun findByCustomerIdOrderByAppliedAtDescCreatedAtDesc(
        customerId: UUID,
        pageable: Pageable,
    ): Page<AssessmentJpaEntity>
}
