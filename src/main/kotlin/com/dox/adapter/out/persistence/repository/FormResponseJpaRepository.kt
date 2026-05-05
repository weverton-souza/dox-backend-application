package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.FormResponseJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FormResponseJpaRepository : JpaRepository<FormResponseJpaEntity, UUID> {
    fun findByFormId(formId: UUID): List<FormResponseJpaEntity>

    fun findByCustomerIdOrderByUpdatedAtDesc(customerId: UUID): List<FormResponseJpaEntity>

    fun findAllByIdIn(ids: List<UUID>): List<FormResponseJpaEntity>

    fun countByFormVersionId(formVersionId: UUID): Long

    fun countByFormId(formId: UUID): Long

    fun findByCustomerIdAndFormIdAndFormVersionId(
        customerId: UUID,
        formId: UUID,
        formVersionId: UUID,
    ): List<FormResponseJpaEntity>
}
