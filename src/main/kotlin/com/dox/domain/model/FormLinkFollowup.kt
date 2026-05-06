package com.dox.domain.model

import com.dox.domain.email.FollowupLevel
import com.dox.domain.enum.FormLinkFollowupStatus
import java.time.LocalDateTime
import java.util.UUID

data class FormLinkFollowup(
    val id: UUID = UUID.randomUUID(),
    val formLinkId: UUID,
    val level: FollowupLevel,
    val dayOffset: Int,
    val scheduledFor: LocalDateTime,
    val status: FormLinkFollowupStatus = FormLinkFollowupStatus.SCHEDULED,
    val emailLogId: UUID? = null,
    val errorMessage: String? = null,
    val sentAt: LocalDateTime? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
