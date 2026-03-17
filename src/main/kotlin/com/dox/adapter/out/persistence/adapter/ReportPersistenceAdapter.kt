package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.ReportJpaEntity
import com.dox.adapter.out.persistence.entity.ReportVersionJpaEntity
import com.dox.adapter.out.persistence.repository.ReportJpaRepository
import com.dox.adapter.out.persistence.repository.ReportVersionJpaRepository
import com.dox.application.port.output.ReportPersistencePort
import com.dox.domain.model.Report
import com.dox.extensions.softDeleteById
import com.dox.domain.model.ReportVersion
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ReportPersistenceAdapter(
    private val reportJpaRepository: ReportJpaRepository,
    private val versionJpaRepository: ReportVersionJpaRepository
) : ReportPersistencePort {

    override fun save(report: Report): Report {
        val entity = reportJpaRepository.findById(report.id).orElse(null)
            ?: ReportJpaEntity().apply { id = report.id }
        entity.status = report.status
        entity.customerName = report.customerName
        entity.customerId = report.customerId
        entity.formResponseId = report.formResponseId
        entity.blocks = report.blocks
        return reportJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: UUID): Report? =
        reportJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findAll(pageable: Pageable): Page<Report> =
        reportJpaRepository.findAll(pageable).map { it.toDomain() }

    override fun findByCustomerId(customerId: UUID): List<Report> =
        reportJpaRepository.findByCustomerId(customerId).map { it.toDomain() }

    override fun softDelete(id: UUID) =
        reportJpaRepository.softDeleteById(id, "Relatório")

    override fun saveVersion(version: ReportVersion): ReportVersion {
        val entity = ReportVersionJpaEntity().apply {
            id = version.id
            reportId = version.reportId
            status = version.status
            description = version.description
            customerName = version.customerName
            blocks = version.blocks
            type = version.type
        }
        return versionJpaRepository.save(entity).toDomain()
    }

    override fun findVersionsByReportId(reportId: UUID): List<ReportVersion> =
        versionJpaRepository.findByReportIdOrderByCreatedAtDesc(reportId).map { it.toDomain() }

    override fun countVersionsByReportId(reportId: UUID): Long =
        versionJpaRepository.countByReportId(reportId)

    override fun deleteOldestVersion(reportId: UUID) {
        val versions = versionJpaRepository.findByReportIdOrderByCreatedAtDesc(reportId)
        if (versions.isNotEmpty()) {
            versionJpaRepository.delete(versions.last())
        }
    }

    private fun ReportJpaEntity.toDomain() = Report(
        id = id, status = status, customerName = customerName,
        customerId = customerId, formResponseId = formResponseId,
        blocks = blocks, deleted = deleted,
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun ReportVersionJpaEntity.toDomain() = ReportVersion(
        id = id, reportId = reportId, status = status,
        description = description, customerName = customerName,
        blocks = blocks, type = type, createdAt = createdAt
    )
}
