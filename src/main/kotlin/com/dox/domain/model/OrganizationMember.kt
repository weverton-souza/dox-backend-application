package com.dox.domain.model

import com.dox.domain.enum.MemberRole
import java.time.LocalDateTime
import java.util.UUID

data class OrganizationMember(
    val id: UUID = UUID.randomUUID(),
    val organizationId: UUID,
    val userId: UUID,
    val role: MemberRole,
    val joinedAt: LocalDateTime? = null,
)
