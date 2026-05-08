package com.dox.adapter.`in`.rest.dto.admin

import com.dox.domain.billing.BillingType
import com.dox.domain.billing.DashboardPeriod
import com.dox.domain.billing.SubscriptionStatus
import com.dox.domain.enum.Vertical
import java.time.LocalDateTime
import java.util.UUID

data class AdminDashboardResponse(
    val period: DashboardPeriod,
    val periodStart: LocalDateTime,
    val periodEnd: LocalDateTime,
    val mrr: KpiCardResponse,
    val arr: KpiCardResponse,
    val activeSubscriptions: KpiCardResponse,
    val trials: KpiCardResponse,
    val signupsInPeriod: KpiCardResponse,
    val overdue: OverdueResponse,
    val revenueLast12Months: List<RevenuePointResponse>,
    val revenueByMethod: List<MethodRevenueResponse>,
    val topModulesByRevenue: List<ModuleRevenueResponse>,
    val recentSignups: List<RecentSignupResponse>,
)

data class KpiCardResponse(
    val current: Long,
    val previous: Long,
    val delta: Long,
    val deltaPct: Double?,
)

data class OverdueResponse(
    val overdueAmountCents: Long,
    val overdueCount: Long,
    val graceCount: Long,
    val suspendedCount: Long,
)

data class RevenuePointResponse(
    val year: Int,
    val month: Int,
    val totalCents: Long,
)

data class MethodRevenueResponse(
    val billingType: BillingType,
    val totalCents: Long,
    val paymentCount: Long,
)

data class ModuleRevenueResponse(
    val moduleId: String,
    val mrrCents: Long,
    val activeCount: Long,
)

data class RecentSignupResponse(
    val tenantId: UUID,
    val tenantName: String,
    val vertical: Vertical,
    val createdAt: LocalDateTime,
    val subscriptionStatus: SubscriptionStatus?,
)
