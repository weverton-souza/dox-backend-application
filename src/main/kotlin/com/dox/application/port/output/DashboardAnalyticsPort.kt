package com.dox.application.port.output

import com.dox.domain.billing.MethodRevenue
import com.dox.domain.billing.ModuleRevenue
import com.dox.domain.billing.OverdueSummary
import com.dox.domain.billing.RecentSignup
import com.dox.domain.billing.RevenuePoint
import java.time.LocalDateTime

interface DashboardAnalyticsPort {
    fun calculateMrrCents(asOf: LocalDateTime): Long

    fun countActiveSubscriptions(asOf: LocalDateTime): Long

    fun countTrialSubscriptions(asOf: LocalDateTime): Long

    fun countSignupsBetween(
        from: LocalDateTime,
        to: LocalDateTime,
    ): Long

    fun overdueSummary(): OverdueSummary

    fun revenueByMonth(monthsBack: Int): List<RevenuePoint>

    fun revenueByMethodBetween(
        from: LocalDateTime,
        to: LocalDateTime,
    ): List<MethodRevenue>

    fun topModulesByMonthlyRevenue(limit: Int): List<ModuleRevenue>

    fun recentSignups(limit: Int): List<RecentSignup>
}
