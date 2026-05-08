package com.dox.domain.billing

import com.dox.domain.enum.Vertical
import java.time.LocalDateTime
import java.util.UUID

enum class DashboardPeriod(
    val days: Int?,
) {
    CURRENT_MONTH(null),
    LAST_30D(30),
    LAST_90D(90),
    LAST_12M(365),
}

data class KpiValue(
    val current: Long,
    val previous: Long,
) {
    val delta: Long get() = current - previous

    val deltaPct: Double? get() =
        previous.takeIf { it > 0L }?.let { (current - it).toDouble() * 100.0 / it.toDouble() }
}

data class OverdueSummary(
    val overdueAmountCents: Long,
    val overdueCount: Long,
    val graceCount: Long,
    val suspendedCount: Long,
)

data class RevenuePoint(
    val year: Int,
    val month: Int,
    val totalCents: Long,
)

data class MethodRevenue(
    val billingType: BillingType,
    val totalCents: Long,
    val paymentCount: Long,
)

data class ModuleRevenue(
    val moduleId: String,
    val mrrCents: Long,
    val activeCount: Long,
)

data class RecentSignup(
    val tenantId: UUID,
    val tenantName: String,
    val vertical: Vertical,
    val createdAt: LocalDateTime,
    val subscriptionStatus: SubscriptionStatus?,
)

data class AdminDashboardSnapshot(
    val period: DashboardPeriod,
    val periodStart: LocalDateTime,
    val periodEnd: LocalDateTime,
    val mrrCents: KpiValue,
    val arrCents: KpiValue,
    val activeSubscriptions: KpiValue,
    val trials: KpiValue,
    val signupsInPeriod: KpiValue,
    val overdue: OverdueSummary,
    val revenueLast12Months: List<RevenuePoint>,
    val revenueByMethod: List<MethodRevenue>,
    val topModulesByRevenue: List<ModuleRevenue>,
    val recentSignups: List<RecentSignup>,
)
