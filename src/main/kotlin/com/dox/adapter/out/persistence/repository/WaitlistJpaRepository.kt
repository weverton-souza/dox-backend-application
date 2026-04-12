package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.WaitlistJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface WaitlistJpaRepository : JpaRepository<WaitlistJpaEntity, UUID> {
    fun existsByEmail(email: String): Boolean
}
