package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.EmailSuppressionJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface EmailSuppressionJpaRepository : JpaRepository<EmailSuppressionJpaEntity, UUID> {
    @Query("SELECT s FROM EmailSuppressionJpaEntity s WHERE LOWER(s.email) = LOWER(:email)")
    fun findByEmailIgnoreCase(
        @Param("email") email: String,
    ): EmailSuppressionJpaEntity?

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END FROM EmailSuppressionJpaEntity s WHERE LOWER(s.email) = LOWER(:email)")
    fun existsByEmailIgnoreCase(
        @Param("email") email: String,
    ): Boolean

    @Modifying
    @Query("DELETE FROM EmailSuppressionJpaEntity s WHERE LOWER(s.email) = LOWER(:email)")
    fun deleteByEmailIgnoreCase(
        @Param("email") email: String,
    ): Int
}
