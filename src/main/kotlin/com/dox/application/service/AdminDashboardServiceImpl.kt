package com.dox.application.service

import com.dox.application.port.input.AdminDashboardUseCase
import com.dox.application.port.output.DashboardAnalyticsPort
import com.dox.domain.billing.AdminDashboardSnapshot
import com.dox.domain.billing.ChurnPoint
import com.dox.domain.billing.DashboardPeriod
import com.dox.domain.billing.KpiValue
import com.dox.domain.billing.RevenueSnapshot
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
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
        private const val MAX_BACKFILL_MONTHS = 36
        private val ZONE_BR: ZoneId = ZoneId.of("America/Sao_Paulo")
    }

    @Transactional(readOnly = true)
    override fun getSnapshot(period: DashboardPeriod): AdminDashboardSnapshot {
        val now = LocalDateTime.now(ZONE_BR)
        val (periodStart, previousStart) = resolvePeriod(period, now)

        val mrrCurrent = analyticsPort.calculateMrrCents(now)
        val mrrPrevious = previousMrr(periodStart, now) ?: analyticsPort.calculateMrrCents(periodStart)

        val activeCurrent = analyticsPort.countActiveSubscriptions(now)
        val activePrevious =
            previousSnapshot(periodStart, now)?.activeSubscriptions?.toLong()
                ?: analyticsPort.countActiveSubscriptions(periodStart)

        val trialsCurrent = analyticsPort.countTrialSubscriptions(now)
        val trialsPrevious =
            previousSnapshot(periodStart, now)?.trialSubscriptions?.toLong()
                ?: analyticsPort.countTrialSubscriptions(periodStart)

        val signupsCurrent = analyticsPort.countSignupsBetween(periodStart, now)
        val signupsPrevious = analyticsPort.countSignupsBetween(previousStart, periodStart)

        val churnSeries = analyticsPort.churnLast12Months()
        val churnRatePct = currentChurnRate(churnSeries)

        val trialConversion = analyticsPort.trialConversionBetween(now.minusDays(30), now)

        val ltvCents = estimateLtvCents(activeCurrent, mrrCurrent, churnRatePct)

        return AdminDashboardSnapshot(
            period = period,
            periodStart = periodStart,
            periodEnd = now,
            mrrCents = KpiValue(mrrCurrent, mrrPrevious),
            arrCents = KpiValue(mrrCurrent * 12L, mrrPrevious * 12L),
            activeSubscriptions = KpiValue(activeCurrent, activePrevious),
            trials = KpiValue(trialsCurrent, trialsPrevious),
            signupsInPeriod = KpiValue(signupsCurrent, signupsPrevious),
            churnRatePct = churnRatePct,
            ltvCents = ltvCents,
            trialConversion = trialConversion,
            overdue = analyticsPort.overdueSummary(),
            revenueLast12Months = analyticsPort.revenueByMonth(MONTHS_OF_REVENUE_HISTORY),
            churnLast12Months = churnSeries,
            revenueByMethod = analyticsPort.revenueByMethodBetween(periodStart, now),
            topModulesByRevenue = analyticsPort.topModulesByMonthlyRevenue(TOP_MODULES_LIMIT),
            recentSignups = analyticsPort.recentSignups(RECENT_SIGNUPS_LIMIT),
        )
    }

    @Transactional
    override fun captureSnapshot(
        year: Int,
        month: Int,
    ): RevenueSnapshot {
        val ym = YearMonth.of(year, month)
        val from = ym.atDay(1).atStartOfDay()
        val to = ym.plusMonths(1).atDay(1).atStartOfDay()

        val mrrAtEnd = analyticsPort.calculateMrrCents(to)
        val activeAtEnd = analyticsPort.countActiveSubscriptions(to).toInt()
        val trialAtEnd = analyticsPort.countTrialSubscriptions(to).toInt()
        val overdue = analyticsPort.overdueSummary()
        val signups = analyticsPort.countSignupsBetween(from, to).toInt()
        val canceled = analyticsPort.countCanceledBetween(from, to).toInt()
        val conv = analyticsPort.trialConversionBetween(from, to)

        val snapshot =
            RevenueSnapshot(
                year = year,
                month = month,
                mrrCents = mrrAtEnd,
                arrCents = mrrAtEnd * 12L,
                activeSubscriptions = activeAtEnd,
                trialSubscriptions = trialAtEnd,
                overdueAmountCents = overdue.overdueAmountCents,
                newSignups = signups,
                canceledSubscriptions = canceled,
                trialStarted = conv.started,
                trialConverted = conv.converted,
                capturedAt = LocalDateTime.now(ZONE_BR),
            )
        analyticsPort.saveSnapshot(snapshot)
        return snapshot
    }

    @Transactional
    override fun backfillSnapshots(months: Int): List<RevenueSnapshot> {
        val safeMonths = months.coerceIn(1, MAX_BACKFILL_MONTHS)
        val today = LocalDate.now(ZONE_BR)
        return (1..safeMonths)
            .map { offset -> today.minusMonths(offset.toLong()) }
            .map { date -> captureSnapshot(date.year, date.monthValue) }
    }

    private fun previousSnapshot(
        periodStart: LocalDateTime,
        now: LocalDateTime,
    ): RevenueSnapshot? {
        val periodSpansAtLeastOneMonth = ChronoUnit.DAYS.between(periodStart, now) >= 28
        if (!periodSpansAtLeastOneMonth) return null
        val target = YearMonth.from(periodStart.minusMonths(0))
        return analyticsPort.findSnapshot(target.year, target.monthValue)
    }

    private fun previousMrr(
        periodStart: LocalDateTime,
        now: LocalDateTime,
    ): Long? = previousSnapshot(periodStart, now)?.mrrCents

    private fun currentChurnRate(series: List<ChurnPoint>): Double? {
        if (series.isEmpty()) return null
        val last = series.last()
        return last.churnRatePct
    }

    private fun estimateLtvCents(
        activeSubs: Long,
        mrrCents: Long,
        churnRatePct: Double?,
    ): Long? {
        if (activeSubs <= 0L || churnRatePct == null) return null
        val arpu = mrrCents.toDouble() / activeSubs.toDouble()
        val churnFraction = churnRatePct / 100.0
        if (churnFraction <= 0.0) return null
        return (arpu / churnFraction).toLong()
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
