package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.FormLinkFollowupJpaEntity
import com.dox.adapter.out.persistence.repository.FormLinkFollowupJpaRepository
import com.dox.application.port.output.FormLinkFollowupPersistencePort
import com.dox.domain.enum.FormLinkFollowupStatus
import com.dox.domain.model.FormLinkFollowup
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class FormLinkFollowupPersistenceAdapter(
    private val repository: FormLinkFollowupJpaRepository,
) : FormLinkFollowupPersistencePort {
    override fun save(followup: FormLinkFollowup): FormLinkFollowup = repository.save(toEntity(followup)).toDomain()

    override fun saveAll(followups: List<FormLinkFollowup>): List<FormLinkFollowup> = repository.saveAll(followups.map { toEntity(it) }).map { it.toDomain() }

    override fun findById(id: UUID): FormLinkFollowup? = repository.findById(id).orElse(null)?.toDomain()

    override fun findByFormLinkId(formLinkId: UUID): List<FormLinkFollowup> = repository.findByFormLinkIdOrderByScheduledForAsc(formLinkId).map { it.toDomain() }

    override fun findScheduledDueBefore(now: LocalDateTime): List<FormLinkFollowup> = repository.findDue(FormLinkFollowupStatus.SCHEDULED, now).map { it.toDomain() }

    private fun toEntity(model: FormLinkFollowup): FormLinkFollowupJpaEntity {
        val entity =
            repository.findById(model.id).orElseGet { FormLinkFollowupJpaEntity(id = model.id) }
        entity.formLinkId = model.formLinkId
        entity.level = model.level
        entity.dayOffset = model.dayOffset
        entity.scheduledFor = model.scheduledFor
        entity.status = model.status
        entity.emailLogId = model.emailLogId
        entity.errorMessage = model.errorMessage
        entity.sentAt = model.sentAt
        return entity
    }

    private fun FormLinkFollowupJpaEntity.toDomain() =
        FormLinkFollowup(
            id = id,
            formLinkId = formLinkId,
            level = level,
            dayOffset = dayOffset,
            scheduledFor = scheduledFor,
            status = status,
            emailLogId = emailLogId,
            errorMessage = errorMessage,
            sentAt = sentAt,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
