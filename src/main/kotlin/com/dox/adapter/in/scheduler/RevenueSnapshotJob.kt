package com.dox.adapter.`in`.scheduler

import com.dox.application.port.input.AdminDashboardUseCase
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class RevenueSnapshotJob(
    private val useCase: AdminDashboardUseCase,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 0 1 1 * *", zone = "America/Sao_Paulo")
    fun captureMonthlySnapshot() {
        val previousMonth = LocalDate.now().minusMonths(1)
        val captured = useCase.captureSnapshot(previousMonth.year, previousMonth.monthValue)
        log.info(
            "Snapshot capturado para {}/{}: MRR={} cents, ativas={}, churn estimado={}",
            captured.year,
            captured.month,
            captured.mrrCents,
            captured.activeSubscriptions,
            captured.canceledSubscriptions,
        )
    }
}
