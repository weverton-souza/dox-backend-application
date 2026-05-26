package com.dox.application.service

import com.dox.adapter.out.persistence.repository.CalendarEventJpaRepository
import com.dox.adapter.out.persistence.repository.ChartTemplateJpaRepository
import com.dox.adapter.out.persistence.repository.CustomerFileJpaRepository
import com.dox.adapter.out.persistence.repository.ScoreTableTemplateJpaRepository
import com.dox.application.port.input.AssessmentEntryCommand
import com.dox.application.port.input.AssessmentUseCase
import com.dox.application.port.input.CreateAssessmentCommand
import com.dox.application.port.input.RelatedTemplate
import com.dox.application.port.input.UpdateAssessmentCommand
import com.dox.application.port.output.AssessmentPersistencePort
import com.dox.domain.enum.AssessmentEntryType
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.Assessment
import com.dox.domain.model.AssessmentEntry
import com.dox.domain.model.AssessmentScore
import com.dox.shared.ContextHolder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

private const val CURRENT_DECLARATION_REVISION = 1

@Service
@Transactional(readOnly = true)
class AssessmentServiceImpl(
    private val assessmentPersistencePort: AssessmentPersistencePort,
    private val calendarEventRepository: CalendarEventJpaRepository,
    private val customerFileRepository: CustomerFileJpaRepository,
    private val scoreTableTemplateRepository: ScoreTableTemplateJpaRepository,
    private val chartTemplateRepository: ChartTemplateJpaRepository,
) : AssessmentUseCase {
    @Transactional
    override fun create(command: CreateAssessmentCommand): Assessment {
        validateCommonFields(
            customerId = command.customerId,
            appointmentId = command.appointmentId,
            parentAssessmentId = command.parentAssessmentId,
            appliedAt = command.appliedAt,
            entries = command.entries,
        )

        val applierId = ContextHolder.getUserIdOrThrow()
        val assessment =
            Assessment(
                customerId = command.customerId,
                appointmentId = command.appointmentId,
                applierId = applierId,
                title = command.title,
                category = command.category,
                appliedAt = command.appliedAt,
                notes = command.notes,
                parentAssessmentId = command.parentAssessmentId,
                professionalDeclarationAcceptedAt = LocalDateTime.now(),
                professionalDeclarationRevision = CURRENT_DECLARATION_REVISION,
                entries = command.entries.toDomainEntries(command.customerId),
            )
        return assessmentPersistencePort.save(assessment)
    }

    override fun findById(id: UUID): Assessment =
        assessmentPersistencePort.findById(id)
            ?: throw ResourceNotFoundException("Avaliação", id.toString())

    override fun findByCustomerId(
        customerId: UUID,
        pageable: Pageable,
    ): Page<Assessment> = assessmentPersistencePort.findByCustomerId(customerId, pageable)

    @Transactional
    override fun update(command: UpdateAssessmentCommand): Assessment {
        val existing =
            assessmentPersistencePort.findById(command.id)
                ?: throw ResourceNotFoundException("Avaliação", command.id.toString())

        if (existing.customerId != command.customerId) {
            throw BusinessException("Avaliação não pertence a este cliente")
        }

        validateCommonFields(
            customerId = command.customerId,
            appointmentId = command.appointmentId,
            parentAssessmentId = command.parentAssessmentId,
            appliedAt = command.appliedAt,
            entries = command.entries,
            assessmentIdBeingUpdated = command.id,
        )

        val updated =
            existing.copy(
                appointmentId = command.appointmentId,
                title = command.title,
                category = command.category,
                appliedAt = command.appliedAt,
                notes = command.notes,
                parentAssessmentId = command.parentAssessmentId,
                entries = command.entries.toDomainEntries(command.customerId, command.id),
            )
        return assessmentPersistencePort.save(updated)
    }

    @Transactional
    override fun delete(id: UUID) {
        assessmentPersistencePort.findById(id)
            ?: throw ResourceNotFoundException("Avaliação", id.toString())
        assessmentPersistencePort.softDelete(id)
    }

    override fun autocompleteInstruments(query: String): List<String> {
        val trimmed = query.trim()
        if (trimmed.length < 2) return emptyList()
        return assessmentPersistencePort.findInstrumentNamesByQuery(trimmed)
    }

    override fun findRelatedTemplates(assessmentId: UUID): List<RelatedTemplate> {
        val assessment =
            assessmentPersistencePort.findById(assessmentId)
                ?: throw ResourceNotFoundException("Avaliação", assessmentId.toString())

        val instrumentNames =
            assessment.entries
                .map { normalizeInstrumentName(it.instrumentName) }
                .filter { it.isNotBlank() }
                .toSet()

        if (instrumentNames.isEmpty()) return emptyList()

        val scoreTables =
            scoreTableTemplateRepository.findAll()
                .filter { template ->
                    template.instrumentName?.let { normalizeInstrumentName(it) in instrumentNames } ?: false
                }
                .map {
                    RelatedTemplate(
                        id = it.id,
                        name = it.name,
                        type = "SCORE_TABLE",
                        instrumentName = it.instrumentName,
                        category = it.category,
                    )
                }

        val charts =
            chartTemplateRepository.findAll()
                .filter { template ->
                    template.instrumentName?.let { normalizeInstrumentName(it) in instrumentNames } ?: false
                }
                .map {
                    RelatedTemplate(
                        id = it.id,
                        name = it.name,
                        type = "CHART",
                        instrumentName = it.instrumentName,
                        category = it.category,
                    )
                }

        return scoreTables + charts
    }

    private fun validateCommonFields(
        customerId: UUID,
        appointmentId: UUID?,
        parentAssessmentId: UUID?,
        appliedAt: LocalDate,
        entries: List<AssessmentEntryCommand>,
        assessmentIdBeingUpdated: UUID? = null,
    ) {
        if (appliedAt.isAfter(LocalDate.now())) {
            throw BusinessException("Data da avaliação não pode ser futura")
        }
        if (entries.isEmpty()) {
            throw BusinessException("Avaliação precisa ter ao menos um registro")
        }
        appointmentId?.let { validateAppointment(it, customerId) }
        parentAssessmentId?.let { validateParentAssessment(it, customerId, assessmentIdBeingUpdated) }
        entries.forEach { validateEntry(it, customerId) }
    }

    private fun validateAppointment(
        appointmentId: UUID,
        customerId: UUID,
    ) {
        val event =
            calendarEventRepository.findById(appointmentId).orElse(null)
                ?: throw ResourceNotFoundException("Atendimento", appointmentId.toString())
        if (event.customerId != customerId) {
            throw BusinessException("Atendimento não pertence a este cliente")
        }
    }

    private fun validateParentAssessment(
        parentId: UUID,
        customerId: UUID,
        assessmentIdBeingUpdated: UUID?,
    ) {
        if (parentId == assessmentIdBeingUpdated) {
            throw BusinessException("Avaliação não pode ser pai de si mesma")
        }
        val parent =
            assessmentPersistencePort.findById(parentId)
                ?: throw ResourceNotFoundException("Avaliação pai", parentId.toString())
        if (parent.customerId != customerId) {
            throw BusinessException("Avaliação pai não pertence a este cliente")
        }
    }

    private fun validateEntry(
        entry: AssessmentEntryCommand,
        customerId: UUID,
    ) {
        if (entry.instrumentName.isBlank()) {
            throw BusinessException("Registro precisa ter nome do instrumento")
        }
        when (entry.entryType) {
            AssessmentEntryType.SIMPLE -> {
                if (entry.block != null) {
                    throw BusinessException("Registro simples não pode ter bloco")
                }
                if (entry.scores.isEmpty() && entry.observations.isNullOrBlank()) {
                    throw BusinessException("Registro simples precisa ter ao menos um escore ou observação")
                }
            }
            AssessmentEntryType.TABLE, AssessmentEntryType.CHART -> {
                if (entry.block == null) {
                    throw BusinessException("Registro do tipo ${entry.entryType.name} precisa de bloco")
                }
            }
        }
        entry.attachmentFileId?.let { validateAttachment(it, customerId) }
    }

    private fun validateAttachment(
        attachmentFileId: UUID,
        customerId: UUID,
    ) {
        val file =
            customerFileRepository.findById(attachmentFileId).orElse(null)
                ?: throw ResourceNotFoundException("Arquivo", attachmentFileId.toString())
        if (file.customerId != customerId) {
            throw BusinessException("Arquivo não pertence a este cliente")
        }
    }

    private fun List<AssessmentEntryCommand>.toDomainEntries(
        customerId: UUID,
        assessmentId: UUID? = null,
    ): List<AssessmentEntry> {
        val parentId = assessmentId ?: UUID.randomUUID()
        return mapIndexed { idx, cmd ->
            AssessmentEntry(
                id = cmd.id ?: UUID.randomUUID(),
                assessmentId = parentId,
                instrumentName = cmd.instrumentName,
                entryType = cmd.entryType,
                orderIndex = idx,
                scores =
                    cmd.scores.map {
                        AssessmentScore(
                            index = it.index,
                            label = it.label,
                            value = it.value,
                            classification = it.classification,
                        )
                    },
                block = cmd.block,
                observations = cmd.observations,
                attachmentFileId = cmd.attachmentFileId,
            )
        }
    }

    private fun normalizeInstrumentName(name: String): String = name.lowercase().replace(Regex("[\\s\\-]+"), "")
}
