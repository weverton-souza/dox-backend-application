package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.ContentLibraryJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ContentLibraryJpaRepository : JpaRepository<ContentLibraryJpaEntity, UUID> {
    @Query(
        "SELECT e FROM ContentLibraryJpaEntity e WHERE " +
            "(LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.instrument) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.authors) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.tags) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:type IS NULL OR e.type = :type) ORDER BY e.type, e.title",
    )
    fun search(query: String, type: String? = null): List<ContentLibraryJpaEntity>

    fun findByTypeOrderByTitleAsc(type: String): List<ContentLibraryJpaEntity>

    fun findAllByOrderByTypeAscTitleAsc(): List<ContentLibraryJpaEntity>
}
