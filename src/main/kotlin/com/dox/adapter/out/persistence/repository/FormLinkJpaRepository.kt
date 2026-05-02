package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.FormLinkJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FormLinkJpaRepository : JpaRepository<FormLinkJpaEntity, UUID> {
    fun findAllByOrderByCreatedAtDesc(): List<FormLinkJpaEntity>

    fun findByCustomerIdOrderByCreatedAtDesc(customerId: UUID): List<FormLinkJpaEntity>

    fun findByCustomerIdAndFormIdAndFormVersionIdOrderByCreatedAtAsc(
        customerId: UUID,
        formId: UUID,
        formVersionId: UUID,
    ): List<FormLinkJpaEntity>
}
