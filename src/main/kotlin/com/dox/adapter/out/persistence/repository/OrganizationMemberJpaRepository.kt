package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.OrganizationMemberJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrganizationMemberJpaRepository : JpaRepository<OrganizationMemberJpaEntity, UUID> {
    fun findByUserId(userId: UUID): List<OrganizationMemberJpaEntity>

    fun findByOrganizationId(organizationId: UUID): List<OrganizationMemberJpaEntity>

    fun existsByOrganizationIdAndUserId(organizationId: UUID, userId: UUID): Boolean
}
