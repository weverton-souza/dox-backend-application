package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.FormLinkFollowupJpaEntity
import com.dox.domain.enum.FormLinkFollowupStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.UUID

interface FormLinkFollowupJpaRepository : JpaRepository<FormLinkFollowupJpaEntity, UUID> {
    @Query(
        """
        SELECT f FROM FormLinkFollowupJpaEntity f
        WHERE f.status = :status
          AND f.scheduledFor <= :before
        ORDER BY f.scheduledFor ASC
        """,
    )
    fun findDue(
        @Param("status") status: FormLinkFollowupStatus,
        @Param("before") before: LocalDateTime,
    ): List<FormLinkFollowupJpaEntity>

    fun findByFormLinkIdOrderByScheduledForAsc(formLinkId: UUID): List<FormLinkFollowupJpaEntity>
}
