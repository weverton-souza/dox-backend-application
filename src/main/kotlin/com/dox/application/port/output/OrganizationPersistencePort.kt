package com.dox.application.port.output

import com.dox.domain.model.Organization
import com.dox.domain.model.OrganizationMember
import java.util.UUID

interface OrganizationPersistencePort {
    fun save(organization: Organization): Organization

    fun findById(id: UUID): Organization?

    fun findMembersByUserId(userId: UUID): List<OrganizationMember>

    fun findMembersByOrganizationId(organizationId: UUID): List<OrganizationMember>

    fun saveMember(member: OrganizationMember): OrganizationMember

    fun existsMember(
        organizationId: UUID,
        userId: UUID,
    ): Boolean

    fun countMembers(organizationId: UUID): Int

    fun findByTenantId(tenantId: UUID): Organization?

    fun findMember(
        organizationId: UUID,
        userId: UUID,
    ): OrganizationMember?
}
