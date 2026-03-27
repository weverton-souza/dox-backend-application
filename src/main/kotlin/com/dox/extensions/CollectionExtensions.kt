package com.dox.extensions

import com.dox.adapter.out.persistence.entity.AbstractJpaEntity
import com.dox.domain.exception.ResourceNotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

inline fun <reified T : AbstractJpaEntity> JpaRepository<T, UUID>.softDeleteById(id: UUID, resourceName: String) {
    val entity = findById(id).orElseThrow { ResourceNotFoundException(resourceName, id.toString()) }
    entity.deleted = true
    save(entity)
}
