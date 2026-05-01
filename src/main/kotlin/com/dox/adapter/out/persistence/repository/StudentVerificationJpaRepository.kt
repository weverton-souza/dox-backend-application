package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.StudentVerificationJpaEntity
import com.dox.domain.billing.StudentVerificationStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface StudentVerificationJpaRepository : JpaRepository<StudentVerificationJpaEntity, UUID> {
    fun findAllByStatus(
        status: StudentVerificationStatus,
        pageable: Pageable,
    ): Page<StudentVerificationJpaEntity>
}
