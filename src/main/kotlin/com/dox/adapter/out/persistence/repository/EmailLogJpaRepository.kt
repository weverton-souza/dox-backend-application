package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.EmailLogJpaEntity
import com.dox.domain.email.EmailLogStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.UUID

interface EmailLogJpaRepository : JpaRepository<EmailLogJpaEntity, UUID> {
    fun findByProviderId(providerId: String): EmailLogJpaEntity?

    fun findByIdempotencyKey(key: String): EmailLogJpaEntity?

    @Query(
        """
        SELECT l FROM EmailLogJpaEntity l
        WHERE (:templateId IS NULL OR l.templateId = :templateId)
          AND (:status IS NULL OR l.status = :status)
          AND (:recipientEmail IS NULL OR LOWER(l.recipientEmail) = LOWER(:recipientEmail))
          AND (:tenantId IS NULL OR l.tenantId = :tenantId)
        ORDER BY l.sentAt DESC
        """,
    )
    fun findFiltered(
        @Param("templateId") templateId: String?,
        @Param("status") status: EmailLogStatus?,
        @Param("recipientEmail") recipientEmail: String?,
        @Param("tenantId") tenantId: UUID?,
        pageable: Pageable,
    ): Page<EmailLogJpaEntity>

    @Modifying
    @Query(
        """
        UPDATE EmailLogJpaEntity l
        SET l.status = :status,
            l.errorMessage = :errorMessage,
            l.updatedAt = :updatedAt
        WHERE l.providerId = :providerId
        """,
    )
    fun updateStatusByProviderId(
        @Param("providerId") providerId: String,
        @Param("status") status: EmailLogStatus,
        @Param("errorMessage") errorMessage: String?,
        @Param("updatedAt") updatedAt: LocalDateTime,
    ): Int
}
