package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.CustomerJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface CustomerJpaRepository : JpaRepository<CustomerJpaEntity, UUID> {
    @Query(
        "SELECT c FROM CustomerJpaEntity c WHERE " +
            "LOWER(CAST(function('jsonb_extract_path_text', c.data, 'name') AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "CAST(function('jsonb_extract_path_text', c.data, 'cpf') AS string) LIKE CONCAT('%', :search, '%')",
    )
    fun searchByNameOrCpf(
        search: String,
        pageable: Pageable,
    ): Page<CustomerJpaEntity>
}
