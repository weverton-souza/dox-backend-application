package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.CustomerNoteJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CustomerNoteJpaRepository : JpaRepository<CustomerNoteJpaEntity, UUID> {
    fun findByCustomerIdOrderByCreatedAtDesc(customerId: UUID): List<CustomerNoteJpaEntity>
}
