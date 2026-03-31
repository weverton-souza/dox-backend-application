package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.ReportJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ReportJpaRepository : JpaRepository<ReportJpaEntity, UUID> {
    fun findByCustomerId(customerId: UUID): List<ReportJpaEntity>

    override fun findAll(pageable: Pageable): Page<ReportJpaEntity>
}
