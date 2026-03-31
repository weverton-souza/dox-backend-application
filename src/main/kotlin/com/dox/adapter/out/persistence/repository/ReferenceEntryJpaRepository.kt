package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.ReferenceEntryJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ReferenceEntryJpaRepository : JpaRepository<ReferenceEntryJpaEntity, UUID> {

    @Query("SELECT r FROM ReferenceEntryJpaEntity r WHERE LOWER(r.text) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(r.instrument) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(r.authors) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY r.authors, r.year")
    fun search(query: String): List<ReferenceEntryJpaEntity>

    fun findAllByOrderByAuthorsAscYearAsc(): List<ReferenceEntryJpaEntity>
}
