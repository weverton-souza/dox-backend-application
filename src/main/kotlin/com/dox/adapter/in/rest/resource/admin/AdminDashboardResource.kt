package com.dox.adapter.`in`.rest.resource.admin

import com.dox.adapter.`in`.rest.dto.admin.AdminDashboardResponse
import com.dox.adapter.`in`.rest.dto.admin.BackfillSnapshotsResponse
import com.dox.adapter.`in`.rest.resource.BaseResource
import com.dox.domain.billing.DashboardPeriod
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Admin · Dashboard", description = "KPIs financeiros e operacionais para o backoffice")
@RequestMapping("/admin/dashboard")
interface AdminDashboardResource : BaseResource {
    @Operation(summary = "Snapshot do dashboard com MRR, ARR, trials, inadimplência e séries de receita")
    @GetMapping
    fun snapshot(
        @RequestParam(required = false, defaultValue = "CURRENT_MONTH") period: DashboardPeriod,
    ): ResponseEntity<AdminDashboardResponse>

    @Operation(summary = "Captura snapshots históricos dos últimos N meses para popular base de churn/LTV")
    @PostMapping("/snapshots/backfill")
    fun backfillSnapshots(
        @RequestParam(required = false, defaultValue = "12") months: Int,
    ): ResponseEntity<BackfillSnapshotsResponse>
}
