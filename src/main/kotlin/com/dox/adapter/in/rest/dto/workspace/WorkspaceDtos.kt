package com.dox.adapter.`in`.rest.dto.workspace

import com.dox.domain.enum.MemberRole
import com.dox.domain.enum.TenantType
import com.dox.domain.enum.Vertical
import java.util.UUID

data class WorkspaceResponse(
    val tenantId: UUID,
    val name: String,
    val type: TenantType,
    val vertical: Vertical,
    val role: MemberRole?
)

data class CreateOrganizationRequest(
    val name: String,
    val description: String? = null,
    val vertical: Vertical = Vertical.GENERAL
)

data class InviteMemberRequest(
    val email: String,
    val role: MemberRole = MemberRole.MEMBER
)
