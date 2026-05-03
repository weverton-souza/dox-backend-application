package com.dox.application.service

import com.dox.application.port.input.CreateOrganizationCommand
import com.dox.application.port.input.InviteMemberCommand
import com.dox.application.port.input.WorkspaceInfo
import com.dox.application.port.input.WorkspaceUseCase
import com.dox.application.port.output.OrganizationPersistencePort
import com.dox.application.port.output.TenantPersistencePort
import com.dox.application.port.output.UserPersistencePort
import com.dox.domain.enum.MemberRole
import com.dox.domain.enum.TenantType
import com.dox.domain.exception.DuplicateResourceException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.Organization
import com.dox.domain.model.OrganizationMember
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class WorkspaceServiceImpl(
    private val userPersistencePort: UserPersistencePort,
    private val tenantPersistencePort: TenantPersistencePort,
    private val tenantProvisioningService: TenantProvisioningService,
    private val organizationPersistencePort: OrganizationPersistencePort,
) : WorkspaceUseCase {
    companion object {
        private const val PERSONAL_WORKSPACE_NAME = "Pessoal"
    }

    override fun listWorkspaces(userId: UUID): List<WorkspaceInfo> {
        val user =
            userPersistencePort.findById(userId)
                ?: throw ResourceNotFoundException("Usuário", userId.toString())

        return listOfNotNull(buildPersonalWorkspace(user.personalTenantId)) +
            buildOrgWorkspaces(userId)
    }

    private fun buildPersonalWorkspace(personalTenantId: UUID?): WorkspaceInfo? {
        val tenant = personalTenantId?.let { tenantPersistencePort.findById(it) } ?: return null
        return WorkspaceInfo(
            tenantId = tenant.id,
            name = PERSONAL_WORKSPACE_NAME,
            type = TenantType.PERSONAL,
            vertical = tenant.vertical,
            role = null,
        )
    }

    private fun buildOrgWorkspaces(userId: UUID): List<WorkspaceInfo> {
        return organizationPersistencePort.findMembersByUserId(userId).mapNotNull { membership ->
            val org = organizationPersistencePort.findById(membership.organizationId) ?: return@mapNotNull null
            val tenant = tenantPersistencePort.findById(org.tenantId) ?: return@mapNotNull null
            WorkspaceInfo(
                tenantId = tenant.id,
                name = org.name,
                type = TenantType.ORGANIZATION,
                vertical = tenant.vertical,
                role = membership.role,
            )
        }
    }

    @Transactional
    override fun createOrganization(command: CreateOrganizationCommand): WorkspaceInfo {
        val tenant =
            tenantProvisioningService.provisionTenant(
                name = command.name,
                type = TenantType.ORGANIZATION,
                vertical = command.vertical,
            )

        val org =
            organizationPersistencePort.save(
                Organization(
                    tenantId = tenant.id,
                    name = command.name,
                    description = command.description,
                ),
            )

        organizationPersistencePort.saveMember(
            OrganizationMember(
                organizationId = org.id,
                userId = command.userId,
                role = MemberRole.OWNER,
            ),
        )

        return WorkspaceInfo(
            tenantId = tenant.id,
            name = org.name,
            type = TenantType.ORGANIZATION,
            vertical = tenant.vertical,
            role = MemberRole.OWNER,
        )
    }

    @Transactional
    override fun inviteMember(command: InviteMemberCommand) {
        val user =
            userPersistencePort.findByEmail(command.email)
                ?: throw ResourceNotFoundException("Usuário")

        if (organizationPersistencePort.existsMember(command.organizationId, user.id)) {
            throw DuplicateResourceException("membro", command.email)
        }

        organizationPersistencePort.saveMember(
            OrganizationMember(
                organizationId = command.organizationId,
                userId = user.id,
                role = command.role,
            ),
        )
    }
}
