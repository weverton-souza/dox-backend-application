package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.ReportJpaEntity
import com.dox.adapter.out.persistence.entity.ReportVersionJpaEntity
import com.dox.adapter.out.persistence.repository.ReportJpaRepository
import com.dox.adapter.out.persistence.repository.ReportVersionJpaRepository
import com.dox.application.port.output.ReportPersistencePort
import com.dox.domain.enum.ReportStatus
import com.dox.domain.exception.BusinessException
import com.dox.domain.model.Report
import com.dox.domain.model.ReportVersion
import com.dox.extensions.softDeleteById
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ReportPersistenceAdapter(
    private val reportJpaRepository: ReportJpaRepository,
    private val versionJpaRepository: ReportVersionJpaRepository,
) : ReportPersistencePort {
    override fun save(report: Report): Report {
        val existing = reportJpaRepository.findById(report.id).orElse(null)
        if (existing != null && existing.status == ReportStatus.FINALIZADO) {
            val unchanged =
                existing.status == report.status &&
                    existing.customerName == report.customerName &&
                    existing.customerId == report.customerId &&
                    existing.formResponseId == report.formResponseId &&
                    existing.templateId == report.templateId &&
                    existing.isStructureLocked == report.isStructureLocked &&
                    existing.blocks == report.blocks &&
                    existing.finalizedAt == report.finalizedAt &&
                    existing.contentHash == report.contentHash
            if (!unchanged) {
                throw BusinessException("Relatório finalizado é imutável (id=${report.id}).")
            }
        }
        val entity = existing ?: ReportJpaEntity().apply { id = report.id }
        entity.status = report.status
        entity.customerName = report.customerName
        entity.customerId = report.customerId
        entity.formResponseId = report.formResponseId
        entity.templateId = report.templateId
        entity.isStructureLocked = report.isStructureLocked
        entity.blocks = report.blocks
        entity.finalizedAt = report.finalizedAt
        entity.contentHash = report.contentHash
        return reportJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: UUID): Report? = reportJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findAll(pageable: Pageable): Page<Report> =
        reportJpaRepository.findAll(
            PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.by(Sort.Direction.DESC, "updatedAt")),
        ).map { it.toDomain() }

    override fun findByCustomerId(customerId: UUID): List<Report> = reportJpaRepository.findByCustomerId(customerId).map { it.toDomain() }

    override fun softDelete(id: UUID) = reportJpaRepository.softDeleteById(id, "Relatório")

    override fun saveVersion(version: ReportVersion): ReportVersion {
        val entity =
            ReportVersionJpaEntity().apply {
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

    override fun findVersionsByReportId(reportId: UUID): List<ReportVersion> = versionJpaRepository.findByReportIdOrderByCreatedAtDesc(reportId).map { it.toDomain() }

    override fun countVersionsByReportId(reportId: UUID): Long = versionJpaRepository.countByReportId(reportId)

    override fun deleteOldestVersion(reportId: UUID) {
        val versions = versionJpaRepository.findByReportIdOrderByCreatedAtDesc(reportId)
        if (versions.isNotEmpty()) {
            versionJpaRepository.delete(versions.last())
        }
    }

    private fun ReportJpaEntity.toDomain() =
        Report(
            id = id, status = status, customerName = customerName,
            customerId = customerId, formResponseId = formResponseId,
            templateId = templateId, isStructureLocked = isStructureLocked,
            blocks = blocks, deleted = deleted,
            finalizedAt = finalizedAt, contentHash = contentHash,
            createdAt = createdAt, updatedAt = updatedAt,
        )

    private fun ReportVersionJpaEntity.toDomain() =
        ReportVersion(
            id = id,
            reportId = reportId,
            status = status,
            description = description,
            customerName = customerName,
            blocks = blocks,
            type = type,
            createdAt = createdAt,
        )
}
