package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.AddonJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AddonJpaRepository : JpaRepository<AddonJpaEntity, String>
