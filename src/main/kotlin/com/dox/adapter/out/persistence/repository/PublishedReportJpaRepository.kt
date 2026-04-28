package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.PublishedReportJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PublishedReportJpaRepository : JpaRepository<PublishedReportJpaEntity, UUID> {
    fun findByVerificationCode(verificationCode: String): PublishedReportJpaEntity?

    fun findByTenantIdAndReportId(
        tenantId: UUID,
        reportId: UUID,
    ): PublishedReportJpaEntity?
}
