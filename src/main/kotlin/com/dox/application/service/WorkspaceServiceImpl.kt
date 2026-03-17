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
class WorkspaceServiceImpl(
    private val userPersistencePort: UserPersistencePort,
    private val tenantPersistencePort: TenantPersistencePort,
    private val tenantProvisioningService: TenantProvisioningService,
    private val organizationPersistencePort: OrganizationPersistencePort
) : WorkspaceUseCase {

    override fun listWorkspaces(userId: UUID): List<WorkspaceInfo> {
        val user = userPersistencePort.findById(userId)
            ?: throw ResourceNotFoundException("Usuário", userId.toString())

        val workspaces = mutableListOf<WorkspaceInfo>()

        if (user.personalTenantId != null) {
            val tenant = tenantPersistencePort.findById(user.personalTenantId)
            if (tenant != null) {
                workspaces.add(
                    WorkspaceInfo(
                        tenantId = tenant.id,
                        name = "Pessoal",
                        type = TenantType.PERSONAL,
                        vertical = tenant.vertical,
                        role = null
                    )
                )
            }
        }

        val memberships = organizationPersistencePort.findMembersByUserId(userId)
        for (membership in memberships) {
            val org = organizationPersistencePort.findById(membership.organizationId) ?: continue
            val tenant = tenantPersistencePort.findById(org.tenantId) ?: continue
            workspaces.add(
                WorkspaceInfo(
                    tenantId = tenant.id,
                    name = org.name,
                    type = TenantType.ORGANIZATION,
                    vertical = tenant.vertical,
                    role = membership.role
                )
            )
        }

        return workspaces
    }

    @Transactional
    override fun createOrganization(command: CreateOrganizationCommand): WorkspaceInfo {
        val tenant = tenantProvisioningService.provisionTenant(
            name = command.name,
            type = TenantType.ORGANIZATION,
            vertical = command.vertical
        )

        val org = organizationPersistencePort.save(
            Organization(
                tenantId = tenant.id,
                name = command.name,
                description = command.description
            )
        )

        organizationPersistencePort.saveMember(
            OrganizationMember(
                organizationId = org.id,
                userId = command.userId,
                role = MemberRole.OWNER
            )
        )

        return WorkspaceInfo(
            tenantId = tenant.id,
            name = org.name,
            type = TenantType.ORGANIZATION,
            vertical = tenant.vertical,
            role = MemberRole.OWNER
        )
    }

    @Transactional
    override fun inviteMember(command: InviteMemberCommand) {
        val user = userPersistencePort.findByEmail(command.email)
            ?: throw ResourceNotFoundException("Usuário")

        if (organizationPersistencePort.existsMember(command.organizationId, user.id)) {
            throw DuplicateResourceException("membro", command.email)
        }

        organizationPersistencePort.saveMember(
            OrganizationMember(
                organizationId = command.organizationId,
                userId = user.id,
                role = command.role
            )
        )
    }
}
