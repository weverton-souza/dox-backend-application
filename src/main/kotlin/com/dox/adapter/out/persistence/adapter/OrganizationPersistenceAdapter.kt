package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.OrganizationJpaEntity
import com.dox.adapter.out.persistence.entity.OrganizationMemberJpaEntity
import com.dox.adapter.out.persistence.entity.TenantJpaEntity
import com.dox.adapter.out.persistence.repository.OrganizationJpaRepository
import com.dox.adapter.out.persistence.repository.OrganizationMemberJpaRepository
import com.dox.application.port.output.OrganizationPersistencePort
import com.dox.domain.model.Organization
import com.dox.domain.model.OrganizationMember
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class OrganizationPersistenceAdapter(
    private val organizationJpaRepository: OrganizationJpaRepository,
    private val memberJpaRepository: OrganizationMemberJpaRepository,
) : OrganizationPersistencePort {
    override fun save(organization: Organization): Organization {
        val entity =
            OrganizationJpaEntity().apply {
                id = organization.id
                tenant = TenantJpaEntity().apply { id = organization.tenantId }
                name = organization.name
                description = organization.description
            }
        return organizationJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: UUID): Organization? = organizationJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findMembersByUserId(userId: UUID): List<OrganizationMember> = memberJpaRepository.findByUserId(userId).map { it.toDomain() }

    override fun findMembersByOrganizationId(organizationId: UUID): List<OrganizationMember> = memberJpaRepository.findByOrganizationId(organizationId).map { it.toDomain() }

    override fun saveMember(member: OrganizationMember): OrganizationMember {
        val entity =
            OrganizationMemberJpaEntity().apply {
                id = member.id
                organizationId = member.organizationId
                userId = member.userId
                role = member.role
            }
        return memberJpaRepository.save(entity).toDomain()
    }

    override fun existsMember(
        organizationId: UUID,
        userId: UUID,
    ): Boolean = memberJpaRepository.existsByOrganizationIdAndUserId(organizationId, userId)

    private fun OrganizationJpaEntity.toDomain() =
        Organization(
            id = id,
            tenantId = tenant.id,
            name = name,
            description = description,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    private fun OrganizationMemberJpaEntity.toDomain() =
        OrganizationMember(
            id = id,
            organizationId = organizationId,
            userId = userId,
            role = role,
            joinedAt = joinedAt,
        )
}
