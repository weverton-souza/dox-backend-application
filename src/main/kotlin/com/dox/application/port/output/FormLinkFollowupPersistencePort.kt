package com.dox.application.port.output

import com.dox.domain.model.FormLinkFollowup
import java.time.LocalDateTime
import java.util.UUID

interface FormLinkFollowupPersistencePort {
    fun save(followup: FormLinkFollowup): FormLinkFollowup

    fun saveAll(followups: List<FormLinkFollowup>): List<FormLinkFollowup>

    fun findById(id: UUID): FormLinkFollowup?

    fun findByFormLinkId(formLinkId: UUID): List<FormLinkFollowup>

    fun findScheduledDueBefore(now: LocalDateTime): List<FormLinkFollowup>
}
