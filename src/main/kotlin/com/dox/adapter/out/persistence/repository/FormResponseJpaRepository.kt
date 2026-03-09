package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.FormResponseJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FormResponseJpaRepository : JpaRepository<FormResponseJpaEntity, UUID> {
    fun findByFormId(formId: UUID): List<FormResponseJpaEntity>
}
