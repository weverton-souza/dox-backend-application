package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.AiInstructionJpaEntity
import com.dox.adapter.out.persistence.repository.AiInstructionJpaRepository
import com.dox.application.port.output.AiInstructionPort
import com.dox.domain.enum.Vertical
import com.dox.domain.model.AiInstruction
import org.springframework.stereotype.Component

@Component
class AiInstructionPersistenceAdapter(
    private val repository: AiInstructionJpaRepository
) : AiInstructionPort {
    override fun findActiveByTypeAndVertical(type: String, vertical: Vertical): AiInstruction? =
        repository.findFirstByTypeAndVerticalAndActiveTrue(type, vertical)?.toDomain()

    override fun findActiveByType(type: String): AiInstruction? =
        repository.findFirstByTypeAndVerticalIsNullAndActiveTrue(type)?.toDomain()

    private fun AiInstructionJpaEntity.toDomain() = AiInstruction(
        id = id,
        type = type,
        vertical = vertical,
        content = content,
        active = active,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
