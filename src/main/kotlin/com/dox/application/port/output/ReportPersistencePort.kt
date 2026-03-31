package com.dox.application.port.output

import com.dox.domain.model.Report
import com.dox.domain.model.ReportVersion
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ReportPersistencePort {
    fun save(report: Report): Report

    fun findById(id: UUID): Report?

    fun findAll(pageable: Pageable): Page<Report>

    fun findByCustomerId(customerId: UUID): List<Report>

    fun softDelete(id: UUID)

    fun saveVersion(version: ReportVersion): ReportVersion

    fun findVersionsByReportId(reportId: UUID): List<ReportVersion>

    fun countVersionsByReportId(reportId: UUID): Long

    fun deleteOldestVersion(reportId: UUID)
}
