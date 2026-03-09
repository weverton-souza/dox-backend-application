package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.CustomerEventJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CustomerEventJpaRepository : JpaRepository<CustomerEventJpaEntity, UUID> {
    fun findByCustomerIdOrderByDateDesc(customerId: UUID): List<CustomerEventJpaEntity>
}
