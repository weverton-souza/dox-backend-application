package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.BundleJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BundleJpaRepository : JpaRepository<BundleJpaEntity, String> {
    fun findAllByActiveTrue(): List<BundleJpaEntity>
}
