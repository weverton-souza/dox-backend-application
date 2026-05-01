package com.dox.application.port.output

import com.dox.domain.billing.Promotion
import java.time.LocalDateTime
import java.util.UUID

interface PromotionPersistencePort {
    fun findById(id: UUID): Promotion?

    fun findByCode(code: String): Promotion?

    fun save(promotion: Promotion): Promotion

    fun listAll(includeArchived: Boolean): List<Promotion>

    fun tryIncrementRedemption(
        promotionId: UUID,
        now: LocalDateTime,
    ): Boolean
}
