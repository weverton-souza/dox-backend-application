package com.dox.adapter.`in`.rest.impl.admin

import com.dox.adapter.`in`.rest.dto.admin.AdminDashboardResponse
import com.dox.adapter.`in`.rest.dto.admin.BackfillSnapshotsResponse
import com.dox.adapter.`in`.rest.dto.admin.ChurnPointResponse
import com.dox.adapter.`in`.rest.dto.admin.KpiCardResponse
import com.dox.adapter.`in`.rest.dto.admin.MethodRevenueResponse
import com.dox.adapter.`in`.rest.dto.admin.ModuleRevenueResponse
import com.dox.adapter.`in`.rest.dto.admin.OverdueResponse
import com.dox.adapter.`in`.rest.dto.admin.RecentSignupResponse
import com.dox.adapter.`in`.rest.dto.admin.RevenuePointResponse
import com.dox.adapter.`in`.rest.dto.admin.RevenueSnapshotResponse
import com.dox.adapter.`in`.rest.dto.admin.TrialConversionResponse
import com.dox.adapter.`in`.rest.resource.admin.AdminDashboardResource
import com.dox.application.port.input.AdminDashboardUseCase
import com.dox.domain.billing.AdminDashboardSnapshot
import com.dox.domain.billing.ChurnPoint
import com.dox.domain.billing.DashboardPeriod
import com.dox.domain.billing.KpiValue
import com.dox.domain.billing.MethodRevenue
import com.dox.domain.billing.ModuleRevenue
import com.dox.domain.billing.OverdueSummary
import com.dox.domain.billing.RecentSignup
import com.dox.domain.billing.RevenuePoint
import com.dox.domain.billing.RevenueSnapshot
import com.dox.domain.billing.TrialConversion
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminDashboardResourceImpl(
    private val useCase: AdminDashboardUseCase,
) : AdminDashboardResource {
    override fun snapshot(period: DashboardPeriod): ResponseEntity<AdminDashboardResponse> = responseEntity(useCase.getSnapshot(period).toResponse())

    override fun backfillSnapshots(months: Int): ResponseEntity<BackfillSnapshotsResponse> =
        responseEntity(
            BackfillSnapshotsResponse(
                captured = useCase.backfillSnapshots(months).map { it.toResponse() },
            ),
        )

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
            churnRatePct = churnRatePct,
            ltvCents = ltvCents,
            trialConversion = trialConversion?.toResponse(),
            overdue = overdue.toResponse(),
            revenueLast12Months = revenueLast12Months.map { it.toResponse() },
            churnLast12Months = churnLast12Months.map { it.toResponse() },
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

    private fun TrialConversion.toResponse() =
        TrialConversionResponse(
            started = started,
            converted = converted,
            pct = pct,
        )

    private fun ChurnPoint.toResponse() =
        ChurnPointResponse(
            year = year,
            month = month,
            churnRatePct = churnRatePct,
            canceled = canceled,
            activeAtStart = activeAtStart,
        )

    private fun RevenueSnapshot.toResponse() =
        RevenueSnapshotResponse(
            year = year,
            month = month,
            mrrCents = mrrCents,
            arrCents = arrCents,
            activeSubscriptions = activeSubscriptions,
            trialSubscriptions = trialSubscriptions,
            overdueAmountCents = overdueAmountCents,
            newSignups = newSignups,
            canceledSubscriptions = canceledSubscriptions,
            trialStarted = trialStarted,
            trialConverted = trialConverted,
            capturedAt = capturedAt,
        )
}
