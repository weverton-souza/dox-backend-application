package com.dox.adapter.`in`.rest.impl.admin

import com.dox.adapter.`in`.rest.dto.admin.AdminDashboardResponse
import com.dox.adapter.`in`.rest.dto.admin.KpiCardResponse
import com.dox.adapter.`in`.rest.dto.admin.MethodRevenueResponse
import com.dox.adapter.`in`.rest.dto.admin.ModuleRevenueResponse
import com.dox.adapter.`in`.rest.dto.admin.OverdueResponse
import com.dox.adapter.`in`.rest.dto.admin.RecentSignupResponse
import com.dox.adapter.`in`.rest.dto.admin.RevenuePointResponse
import com.dox.adapter.`in`.rest.resource.admin.AdminDashboardResource
import com.dox.application.port.input.AdminDashboardUseCase
import com.dox.domain.billing.AdminDashboardSnapshot
import com.dox.domain.billing.DashboardPeriod
import com.dox.domain.billing.KpiValue
import com.dox.domain.billing.MethodRevenue
import com.dox.domain.billing.ModuleRevenue
import com.dox.domain.billing.OverdueSummary
import com.dox.domain.billing.RecentSignup
import com.dox.domain.billing.RevenuePoint
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminDashboardResourceImpl(
    private val useCase: AdminDashboardUseCase,
) : AdminDashboardResource {
    override fun snapshot(period: DashboardPeriod): ResponseEntity<AdminDashboardResponse> = responseEntity(useCase.getSnapshot(period).toResponse())

    private fun AdminDashboardSnapshot.toResponse() =
        AdminDashboardResponse(
            period = period,
            periodStart = periodStart,
            periodEnd = periodEnd,
            mrr = mrrCents.toResponse(),
            arr = arrCents.toResponse(),
            activeSubscriptions = activeSubscriptions.toResponse(),
            trials = trials.toResponse(),
            signupsInPeriod = signupsInPeriod.toResponse(),
            overdue = overdue.toResponse(),
            revenueLast12Months = revenueLast12Months.map { it.toResponse() },
            revenueByMethod = revenueByMethod.map { it.toResponse() },
            topModulesByRevenue = topModulesByRevenue.map { it.toResponse() },
            recentSignups = recentSignups.map { it.toResponse() },
        )

    private fun KpiValue.toResponse() =
        KpiCardResponse(
            current = current,
            previous = previous,
            delta = delta,
            deltaPct = deltaPct,
        )

    private fun OverdueSummary.toResponse() =
        OverdueResponse(
            overdueAmountCents = overdueAmountCents,
            overdueCount = overdueCount,
            graceCount = graceCount,
            suspendedCount = suspendedCount,
        )

    private fun RevenuePoint.toResponse() =
        RevenuePointResponse(
            year = year,
            month = month,
            totalCents = totalCents,
        )

    private fun MethodRevenue.toResponse() =
        MethodRevenueResponse(
            billingType = billingType,
            totalCents = totalCents,
            paymentCount = paymentCount,
        )

    private fun ModuleRevenue.toResponse() =
        ModuleRevenueResponse(
            moduleId = moduleId,
            mrrCents = mrrCents,
            activeCount = activeCount,
        )

    private fun RecentSignup.toResponse() =
        RecentSignupResponse(
            tenantId = tenantId,
            tenantName = tenantName,
            vertical = vertical,
            createdAt = createdAt,
            subscriptionStatus = subscriptionStatus,
        )
}
