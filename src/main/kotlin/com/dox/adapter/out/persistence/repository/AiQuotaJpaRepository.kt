package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.AiQuotaJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AiQuotaJpaRepository : JpaRepository<AiQuotaJpaEntity, UUID> {
    fun findFirstByOrderByCreatedAtAsc(): AiQuotaJpaEntity?
}
