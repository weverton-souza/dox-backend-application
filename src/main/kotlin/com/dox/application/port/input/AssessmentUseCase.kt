package com.dox.application.port.input

import com.dox.domain.enum.AssessmentEntryType
import com.dox.domain.model.Assessment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.util.UUID

data class AssessmentScoreCommand(
    val index: String,
    val label: String,
    val value: String,
    val classification: String? = null,
)

data class AssessmentEntryCommand(
    val id: UUID? = null,
    val instrumentName: String,
    val entryType: AssessmentEntryType,
    val orderIndex: Int = 0,
    val scores: List<AssessmentScoreCommand> = emptyList(),
    val block: Map<String, Any?>? = null,
    val observations: String? = null,
    val attachmentFileId: UUID? = null,
)

data class CreateAssessmentCommand(
    val customerId: UUID,
    val appointmentId: UUID? = null,
    val title: String,
    val category: String? = null,
    val appliedAt: LocalDate,
    val notes: String? = null,
    val parentAssessmentId: UUID? = null,
    val entries: List<AssessmentEntryCommand> = emptyList(),
)

data class UpdateAssessmentCommand(
    val id: UUID,
    val customerId: UUID,
    val appointmentId: UUID? = null,
    val title: String,
    val category: String? = null,
    val appliedAt: LocalDate,
    val notes: String? = null,
    val parentAssessmentId: UUID? = null,
    val entries: List<AssessmentEntryCommand> = emptyList(),
)

data class RelatedTemplate(
    val id: UUID,
    val name: String,
    val type: String,
    val instrumentName: String?,
    val category: String?,
)

interface AssessmentUseCase {
    fun create(command: CreateAssessmentCommand): Assessment

    fun findById(id: UUID): Assessment

    fun findByCustomerId(
        customerId: UUID,
        pageable: Pageable,
    ): Page<Assessment>

    fun update(command: UpdateAssessmentCommand): Assessment

    fun delete(id: UUID)

    fun autocompleteInstruments(query: String): List<String>

    fun findRelatedTemplates(assessmentId: UUID): List<RelatedTemplate>
}
