package com.dox.application.service

import com.dox.application.port.input.CreateReportCommand
import com.dox.application.port.input.CreateVersionCommand
import com.dox.application.port.input.ReportUseCase
import com.dox.application.port.input.UpdateReportCommand
import com.dox.application.port.output.ReportPersistencePort
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.Report
import com.dox.domain.model.ReportVersion
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ReportServiceImpl(
    private val reportPersistencePort: ReportPersistencePort
) : ReportUseCase {

    companion object {
        private const val MAX_VERSIONS = 20
    }

    @Transactional
    override fun create(command: CreateReportCommand): Report =
        reportPersistencePort.save(
            Report(
                customerName = command.customerName,
                customerId = command.customerId,
                formResponseId = command.formResponseId,
                blocks = command.blocks
            )
        )

    override fun findById(id: UUID): Report =
        reportPersistencePort.findById(id)
            ?: throw ResourceNotFoundException("Relatório", id.toString())

    override fun findAll(pageable: Pageable): Page<Report> =
        reportPersistencePort.findAll(pageable)

    override fun findByCustomerId(customerId: UUID): List<Report> =
        reportPersistencePort.findByCustomerId(customerId)

    @Transactional
    override fun update(command: UpdateReportCommand): Report {
        val existing = reportPersistencePort.findById(command.id)
            ?: throw ResourceNotFoundException("Relatório", command.id.toString())

        return reportPersistencePort.save(
            existing.copy(
                status = command.status ?: existing.status,
                customerName = command.customerName ?: existing.customerName,
                blocks = command.blocks ?: existing.blocks
            )
        )
    }

    @Transactional
    override fun delete(id: UUID) {
        reportPersistencePort.findById(id)
            ?: throw ResourceNotFoundException("Relatório", id.toString())
        reportPersistencePort.softDelete(id)
    }

    override fun getVersions(reportId: UUID): List<ReportVersion> =
        reportPersistencePort.findVersionsByReportId(reportId)

    @Transactional
    override fun createVersion(command: CreateVersionCommand): ReportVersion {
        val report = reportPersistencePort.findById(command.reportId)
            ?: throw ResourceNotFoundException("Relatório", command.reportId.toString())

        if (reportPersistencePort.countVersionsByReportId(command.reportId) >= MAX_VERSIONS) {
            reportPersistencePort.deleteOldestVersion(command.reportId)
        }

        return reportPersistencePort.saveVersion(
            ReportVersion(
                reportId = report.id,
                status = report.status,
                description = command.description,
                customerName = report.customerName,
                blocks = report.blocks,
                type = command.type
            )
        )
    }

}
