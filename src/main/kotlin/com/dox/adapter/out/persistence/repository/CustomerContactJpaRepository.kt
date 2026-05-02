package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.CustomerContactJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CustomerContactJpaRepository : JpaRepository<CustomerContactJpaEntity, UUID> {
    fun findByCustomerIdOrderByCreatedAtDesc(customerId: UUID): List<CustomerContactJpaEntity>
}
