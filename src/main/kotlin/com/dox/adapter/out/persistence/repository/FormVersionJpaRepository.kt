package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.FormVersionJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FormVersionJpaRepository : JpaRepository<FormVersionJpaEntity, UUID> {
    fun findByFormId(formId: UUID): List<FormVersionJpaEntity>
    fun findByFormIdAndVersion(formId: UUID, version: Int): FormVersionJpaEntity?
}
