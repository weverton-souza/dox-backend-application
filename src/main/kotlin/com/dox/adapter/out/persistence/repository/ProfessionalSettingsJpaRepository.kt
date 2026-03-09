package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.ProfessionalSettingsJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProfessionalSettingsJpaRepository : JpaRepository<ProfessionalSettingsJpaEntity, UUID>
