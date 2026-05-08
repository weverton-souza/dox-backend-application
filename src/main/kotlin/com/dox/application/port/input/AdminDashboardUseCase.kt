package com.dox.application.port.input

import com.dox.domain.billing.AdminDashboardSnapshot
import com.dox.domain.billing.DashboardPeriod

interface AdminDashboardUseCase {
    fun getSnapshot(period: DashboardPeriod): AdminDashboardSnapshot
}
