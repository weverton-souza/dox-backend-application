package com.dox.extensions

import com.dox.adapter.out.persistence.entity.AbstractJpaEntity
import com.dox.domain.exception.ResourceNotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

inline fun <reified T> JpaRepository<T, UUID>.findByIdOrThrow(id: UUID): T =
    findById(id).orElseThrow {
        ResourceNotFoundException(T::class.simpleName ?: "Recurso", id.toString())
    }

inline fun <reified T> JpaRepository<T, UUID>.existOrThrow(id: UUID): Boolean {
    if (!existsById(id)) {
        throw ResourceNotFoundException(T::class.simpleName ?: "Recurso", id.toString())
    }
    return true
}

inline fun <reified T> JpaRepository<T, UUID>.deleteByIdOrThrow(id: UUID) {
    existOrThrow(id)
    deleteById(id)
}

inline fun <reified T : AbstractJpaEntity> JpaRepository<T, UUID>.softDeleteById(id: UUID, resourceName: String) {
    val entity = findById(id).orElseThrow { ResourceNotFoundException(resourceName, id.toString()) }
    entity.deleted = true
    save(entity)
}
