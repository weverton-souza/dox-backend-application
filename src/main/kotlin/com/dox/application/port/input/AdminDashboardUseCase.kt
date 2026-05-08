package com.dox.application.port.input

import com.dox.domain.billing.AdminDashboardSnapshot
import com.dox.domain.billing.DashboardPeriod
import com.dox.domain.billing.RevenueSnapshot

interface AdminDashboardUseCase {
    fun getSnapshot(period: DashboardPeriod): AdminDashboardSnapshot

    fun captureSnapshot(
        year: Int,
        month: Int,
    ): RevenueSnapshot

    fun backfillSnapshots(months: Int): List<RevenueSnapshot>
}
