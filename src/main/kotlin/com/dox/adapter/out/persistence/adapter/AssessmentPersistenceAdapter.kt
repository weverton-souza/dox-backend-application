package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.AssessmentEntryJpaEntity
import com.dox.adapter.out.persistence.entity.AssessmentJpaEntity
import com.dox.adapter.out.persistence.repository.AssessmentEntryJpaRepository
import com.dox.adapter.out.persistence.repository.AssessmentJpaRepository
import com.dox.application.port.output.AssessmentPersistencePort
import com.dox.domain.enum.AssessmentEntryType
import com.dox.domain.model.Assessment
import com.dox.domain.model.AssessmentEntry
import com.dox.domain.model.AssessmentScore
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class AssessmentPersistenceAdapter(
    private val assessmentRepository: AssessmentJpaRepository,
    private val entryRepository: AssessmentEntryJpaRepository,
) : AssessmentPersistencePort {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional
    override fun save(assessment: Assessment): Assessment {
        val saved = assessmentRepository.save(assessment.toEntity())
        entryRepository.deleteByAssessmentId(saved.id)
        entryRepository.flush()
        val savedEntries =
            assessment.entries.mapIndexed { idx, entry ->
                entryRepository.save(entry.toEntity(saved.id, idx))
            }
        return saved.toDomain(savedEntries.map { it.toDomain() })
    }

    override fun findById(id: UUID): Assessment? {
        val entity = assessmentRepository.findById(id).orElse(null) ?: return null
        val entries = entryRepository.findByAssessmentIdOrderByOrderIndexAsc(id)
        return entity.toDomain(entries.map { it.toDomain() })
    }

    override fun findByCustomerId(
        customerId: UUID,
        pageable: Pageable,
    ): Page<Assessment> {
        val page = assessmentRepository.findByCustomerIdOrderByAppliedAtDescCreatedAtDesc(customerId, pageable)
        if (page.isEmpty) return PageImpl(emptyList(), pageable, page.totalElements)

        val ids = page.content.map { it.id }
        val entriesByAssessment =
            entryRepository.findByAssessmentIdIn(ids)
                .groupBy { it.assessmentId }

        return PageImpl(
            page.content.map { entity ->
                val entries =
                    entriesByAssessment[entity.id]
                        ?.sortedBy { it.orderIndex }
                        ?.map { it.toDomain() }
                        ?: emptyList()
                entity.toDomain(entries)
            },
            pageable,
            page.totalElements,
        )
    }

    @Transactional
    override fun softDelete(id: UUID) {
        val entity = assessmentRepository.findById(id).orElse(null) ?: return
        entity.deleted = true
        assessmentRepository.save(entity)
    }

    override fun findInstrumentNamesByQuery(query: String): List<String> {
        val normalized = "%${query.trim().lowercase()}%"
        val sql =
            """
            SELECT DISTINCT instrument_name FROM (
                SELECT instrument_name FROM score_table_templates
                WHERE instrument_name IS NOT NULL
                  AND LOWER(instrument_name) LIKE :q
                UNION
                SELECT instrument_name FROM chart_templates
                WHERE instrument_name IS NOT NULL
                  AND LOWER(instrument_name) LIKE :q
            ) AS combined
            ORDER BY instrument_name ASC
            LIMIT 20
            """.trimIndent()

        @Suppress("UNCHECKED_CAST")
        return entityManager.createNativeQuery(sql)
            .setParameter("q", normalized)
            .resultList as List<String>
    }

    private fun Assessment.toEntity() =
        AssessmentJpaEntity().apply {
            id = this@toEntity.id
            customerId = this@toEntity.customerId
            appointmentId = this@toEntity.appointmentId
            applierId = this@toEntity.applierId
            title = this@toEntity.title
            category = this@toEntity.category
            appliedAt = this@toEntity.appliedAt
            notes = this@toEntity.notes
            parentAssessmentId = this@toEntity.parentAssessmentId
            professionalDeclarationAcceptedAt = this@toEntity.professionalDeclarationAcceptedAt
            professionalDeclarationRevision = this@toEntity.professionalDeclarationRevision
        }

    private fun AssessmentEntry.toEntity(
        parentId: UUID,
        index: Int,
    ) = AssessmentEntryJpaEntity().apply {
        id = this@toEntity.id
        assessmentId = parentId
        instrumentName = this@toEntity.instrumentName
        entryType = this@toEntity.entryType.name
        orderIndex = index
        scores = this@toEntity.scores.map { it.toMap() }
        block = this@toEntity.block
        observations = this@toEntity.observations
        attachmentFileId = this@toEntity.attachmentFileId
    }

    private fun AssessmentScore.toMap(): Map<String, Any?> =
        mapOf(
            "index" to index,
            "label" to label,
            "value" to value,
            "classification" to classification,
        )

    private fun AssessmentJpaEntity.toDomain(entries: List<AssessmentEntry>) =
        Assessment(
            id = id,
            customerId = customerId,
            appointmentId = appointmentId,
            applierId = applierId,
            title = title,
            category = category,
            appliedAt = appliedAt,
            notes = notes,
            parentAssessmentId = parentAssessmentId,
            professionalDeclarationAcceptedAt = professionalDeclarationAcceptedAt,
            professionalDeclarationRevision = professionalDeclarationRevision,
            entries = entries,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    private fun AssessmentEntryJpaEntity.toDomain() =
        AssessmentEntry(
            id = id,
            assessmentId = assessmentId,
            instrumentName = instrumentName,
            entryType = AssessmentEntryType.valueOf(entryType),
            orderIndex = orderIndex,
            scores = scores.map { it.toAssessmentScore() },
            block = block,
            observations = observations,
            attachmentFileId = attachmentFileId,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    private fun Map<String, Any?>.toAssessmentScore() =
        AssessmentScore(
            index = this["index"]?.toString().orEmpty(),
            label = this["label"]?.toString().orEmpty(),
            value = this["value"]?.toString().orEmpty(),
            classification = this["classification"]?.toString(),
        )
}
