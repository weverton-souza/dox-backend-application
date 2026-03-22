package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.OrganizationJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrganizationJpaRepository : JpaRepository<OrganizationJpaEntity, UUID>
