package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.AiGenerationSourceJpaEntity
import com.dox.adapter.out.persistence.repository.AiGenerationSourceJpaRepository
import com.dox.application.port.output.AiGenerationSourcePersistencePort
import com.dox.domain.model.AiGenerationSource
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AiGenerationSourcePersistenceAdapter(
    private val repository: AiGenerationSourceJpaRepository,
) : AiGenerationSourcePersistencePort {
    override fun saveAll(sources: List<AiGenerationSource>): List<AiGenerationSource> = repository.saveAll(sources.map { it.toEntity() }).map { it.toDomain() }

    override fun findByReportId(reportId: UUID): List<AiGenerationSource> = repository.findByReportId(reportId).map { it.toDomain() }

    override fun findByGenerationId(generationId: UUID): List<AiGenerationSource> = repository.findByGenerationId(generationId).map { it.toDomain() }

    private fun AiGenerationSource.toEntity() =
        AiGenerationSourceJpaEntity().apply {
            id = this@toEntity.id
            reportId = this@toEntity.reportId
            generationId = this@toEntity.generationId
            sourceType = this@toEntity.sourceType
            sourceId = this@toEntity.sourceId
            sourceLabel = this@toEntity.sourceLabel
            included = this@toEntity.included
            displayOrder = this@toEntity.displayOrder
        }

    private fun AiGenerationSourceJpaEntity.toDomain() =
        AiGenerationSource(
            id = id,
            reportId = reportId,
            generationId = generationId,
            sourceType = sourceType,
            sourceId = sourceId,
            sourceLabel = sourceLabel,
            included = included,
            displayOrder = displayOrder,
            createdAt = createdAt,
        )
}
