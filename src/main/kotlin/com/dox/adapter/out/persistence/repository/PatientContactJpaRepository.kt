package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.PatientContactJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PatientContactJpaRepository : JpaRepository<PatientContactJpaEntity, UUID> {
    fun findByCustomerIdOrderByCreatedAtDesc(customerId: UUID): List<PatientContactJpaEntity>
}
