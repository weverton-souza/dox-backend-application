package com.dox.application.service

import com.dox.application.port.input.AdminDashboardUseCase
import com.dox.application.port.output.DashboardAnalyticsPort
import com.dox.domain.billing.AdminDashboardSnapshot
import com.dox.domain.billing.DashboardPeriod
import com.dox.domain.billing.KpiValue
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Service
class AdminDashboardServiceImpl(
    private val analyticsPort: DashboardAnalyticsPort,
) : AdminDashboardUseCase {
    companion object {
        private const val MONTHS_OF_REVENUE_HISTORY = 12
        private const val TOP_MODULES_LIMIT = 10
        private const val RECENT_SIGNUPS_LIMIT = 10
        private val ZONE_BR: ZoneId = ZoneId.of("America/Sao_Paulo")
    }

    @Transactional(readOnly = true)
    override fun getSnapshot(period: DashboardPeriod): AdminDashboardSnapshot {
        val now = LocalDateTime.now(ZONE_BR)
        val (periodStart, previousStart) = resolvePeriod(period, now)

        val mrrCurrent = analyticsPort.calculateMrrCents(now)
        val mrrPrevious = analyticsPort.calculateMrrCents(periodStart)

        val activeCurrent = analyticsPort.countActiveSubscriptions(now)
        val activePrevious = analyticsPort.countActiveSubscriptions(periodStart)

        val trialsCurrent = analyticsPort.countTrialSubscriptions(now)
        val trialsPrevious = analyticsPort.countTrialSubscriptions(periodStart)

        val signupsCurrent = analyticsPort.countSignupsBetween(periodStart, now)
        val signupsPrevious = analyticsPort.countSignupsBetween(previousStart, periodStart)

        return AdminDashboardSnapshot(
            period = period,
            periodStart = periodStart,
            periodEnd = now,
            mrrCents = KpiValue(mrrCurrent, mrrPrevious),
            arrCents = KpiValue(mrrCurrent * 12L, mrrPrevious * 12L),
            activeSubscriptions = KpiValue(activeCurrent, activePrevious),
            trials = KpiValue(trialsCurrent, trialsPrevious),
            signupsInPeriod = KpiValue(signupsCurrent, signupsPrevious),
            overdue = analyticsPort.overdueSummary(),
            revenueLast12Months = analyticsPort.revenueByMonth(MONTHS_OF_REVENUE_HISTORY),
            revenueByMethod = analyticsPort.revenueByMethodBetween(periodStart, now),
            topModulesByRevenue = analyticsPort.topModulesByMonthlyRevenue(TOP_MODULES_LIMIT),
            recentSignups = analyticsPort.recentSignups(RECENT_SIGNUPS_LIMIT),
        )
    }

    private fun resolvePeriod(
        period: DashboardPeriod,
        now: LocalDateTime,
    ): Pair<LocalDateTime, LocalDateTime> =
        when (period) {
            DashboardPeriod.CURRENT_MONTH -> {
                val periodStart = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS)
                val previousStart = periodStart.minusMonths(1)
                periodStart to previousStart
            }
            else -> {
                val days = period.days!!.toLong()
                val periodStart = now.minusDays(days)
                val previousStart = now.minusDays(days * 2)
                periodStart to previousStart
            }
        }
}
