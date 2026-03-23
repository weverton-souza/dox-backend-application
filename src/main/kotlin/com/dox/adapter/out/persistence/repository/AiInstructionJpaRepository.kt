package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.AiInstructionJpaEntity
import com.dox.domain.enum.Vertical
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AiInstructionJpaRepository : JpaRepository<AiInstructionJpaEntity, UUID> {
    fun findFirstByTypeAndVerticalAndActiveTrue(type: String, vertical: Vertical): AiInstructionJpaEntity?
    fun findFirstByTypeAndVerticalIsNullAndActiveTrue(type: String): AiInstructionJpaEntity?
}
