package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.CustomerFileJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CustomerFileJpaRepository : JpaRepository<CustomerFileJpaEntity, UUID> {
    fun findByCustomerIdAndDeletedFalse(customerId: UUID): List<CustomerFileJpaEntity>
}
