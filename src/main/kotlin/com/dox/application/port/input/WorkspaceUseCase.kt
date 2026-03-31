package com.dox.application.port.input

import com.dox.domain.enum.MemberRole
import com.dox.domain.enum.TenantType
import com.dox.domain.enum.Vertical
import java.util.UUID

data class WorkspaceInfo(
    val tenantId: UUID,
    val name: String,
    val type: TenantType,
    val vertical: Vertical,
    val role: MemberRole?
)

data class CreateOrganizationCommand(
    val userId: UUID,
    val name: String,
    val description: String?,
    val vertical: Vertical = Vertical.GENERAL
)

data class InviteMemberCommand(
    val organizationId: UUID,
    val email: String,
    val role: MemberRole
)

interface WorkspaceUseCase {
    fun listWorkspaces(userId: UUID): List<WorkspaceInfo>

    fun createOrganization(command: CreateOrganizationCommand): WorkspaceInfo

    fun inviteMember(command: InviteMemberCommand)
}
