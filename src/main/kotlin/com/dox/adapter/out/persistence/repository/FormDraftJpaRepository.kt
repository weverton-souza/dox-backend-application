package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.FormDraftJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FormDraftJpaRepository : JpaRepository<FormDraftJpaEntity, UUID>
