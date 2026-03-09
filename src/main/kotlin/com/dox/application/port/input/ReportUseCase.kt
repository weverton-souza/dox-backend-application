package com.dox.application.port.input

import com.dox.domain.enum.ReportStatus
import com.dox.domain.model.Report
import com.dox.domain.model.ReportVersion
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

data class CreateReportCommand(
    val customerName: String? = null,
    val customerId: UUID? = null,
    val blocks: List<Map<String, Any?>> = emptyList()
)

data class UpdateReportCommand(
    val id: UUID,
    val status: ReportStatus? = null,
    val customerName: String? = null,
    val blocks: List<Map<String, Any?>>? = null
)

data class CreateVersionCommand(
    val reportId: UUID,
    val description: String? = null,
    val type: String = "manual"
)

interface ReportUseCase {
    fun create(command: CreateReportCommand): Report
    fun findById(id: UUID): Report
    fun findAll(pageable: Pageable): Page<Report>
    fun findByCustomerId(customerId: UUID): List<Report>
    fun update(command: UpdateReportCommand): Report
    fun delete(id: UUID)

    fun getVersions(reportId: UUID): List<ReportVersion>
    fun createVersion(command: CreateVersionCommand): ReportVersion
}
