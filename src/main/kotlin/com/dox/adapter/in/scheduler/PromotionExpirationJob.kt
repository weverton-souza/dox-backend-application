package com.dox.adapter.`in`.scheduler

import com.dox.application.port.output.TenantPromotionPersistencePort
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class PromotionExpirationJob(
    private val tenantPromotionPersistencePort: TenantPromotionPersistencePort,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 0 1 * * *")
    fun expirePromotions() {
        val now = LocalDateTime.now()
        val expired = tenantPromotionPersistencePort.markExpiredOlderThan(now)
        if (expired > 0) {
            log.info("Promoções expiradas: {}", expired)
        }
    }
}
