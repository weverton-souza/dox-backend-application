package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.EventTagJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface EventTagJpaRepository : JpaRepository<EventTagJpaEntity, UUID>
