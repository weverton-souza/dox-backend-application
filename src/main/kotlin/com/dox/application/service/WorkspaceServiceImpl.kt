package com.dox.application.service

import com.dox.application.port.input.CreateOrganizationCommand
import com.dox.application.port.input.InviteMemberCommand
import com.dox.application.port.input.WorkspaceInfo
import com.dox.application.port.input.WorkspaceUseCase
import com.dox.application.port.output.BundlePricePersistencePort
import com.dox.application.port.output.OrganizationPersistencePort
import com.dox.application.port.output.SubscriptionPersistencePort
import com.dox.application.port.output.TenantAddonPersistencePort
import com.dox.application.port.output.TenantPersistencePort
import com.dox.application.port.output.UserPersistencePort
import com.dox.domain.enum.MemberRole
import com.dox.domain.enum.TenantType
import com.dox.domain.exception.AccessDeniedException
import com.dox.domain.exception.BusinessException
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
    private val subscriptionPersistencePort: SubscriptionPersistencePort,
    private val bundlePricePersistencePort: BundlePricePersistencePort,
    private val tenantAddonPersistencePort: TenantAddonPersistencePort,
) : WorkspaceUseCase {
    companion object {
        private const val PERSONAL_WORKSPACE_NAME = "Pessoal"
        private const val DEFAULT_SEATS_WITHOUT_SUBSCRIPTION = 1
        private const val EXTRA_SEAT_ADDON_ID = "extra_seat"
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
            organizationId = null,
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
                organizationId = org.id,
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
            organizationId = org.id,
            name = org.name,
            type = TenantType.ORGANIZATION,
            vertical = tenant.vertical,
            role = MemberRole.OWNER,
        )
    }

    @Transactional
    override fun inviteMember(command: InviteMemberCommand) {
        val actor = organizationPersistencePort.findMember(command.organizationId, command.actorUserId)
        if (actor == null || (actor.role != MemberRole.OWNER && actor.role != MemberRole.ADMIN)) {
            throw AccessDeniedException("Apenas OWNER ou ADMIN podem convidar membros")
        }

        val user =
            userPersistencePort.findByEmail(command.email)
                ?: throw BusinessException("O usuário precisa ter uma conta DOX para ser convidado")

        if (organizationPersistencePort.existsMember(command.organizationId, user.id)) {
            throw DuplicateResourceException("membro", command.email)
        }

        enforceSeatLimit(command.organizationId)

        organizationPersistencePort.saveMember(
            OrganizationMember(
                organizationId = command.organizationId,
                userId = user.id,
                role = command.role,
            ),
        )
    }

    private fun enforceSeatLimit(organizationId: UUID) {
        val org =
            organizationPersistencePort.findById(organizationId)
                ?: throw ResourceNotFoundException("Organização", organizationId.toString())

        val seatLimit = resolveSeatLimit(org.tenantId)
        if (organizationPersistencePort.countMembers(organizationId) >= seatLimit) {
            throw BusinessException(
                "Limite de assentos do plano atingido ($seatLimit). Faça upgrade do plano para adicionar mais profissionais.",
            )
        }
    }

    private fun resolveSeatLimit(tenantId: UUID): Int {
        val subscription = subscriptionPersistencePort.findByTenantId(tenantId)
        val baseSeats =
            subscription?.bundlePriceId?.let { bundlePricePersistencePort.findById(it)?.seatsIncluded }
                ?: DEFAULT_SEATS_WITHOUT_SUBSCRIPTION
        val extraSeats = tenantAddonPersistencePort.activeQuantity(tenantId, EXTRA_SEAT_ADDON_ID)
        return baseSeats + extraSeats
    }
}
