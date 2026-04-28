package com.dox.application.service

import com.dox.application.port.input.CreateReportCommand
import com.dox.application.port.input.CreateVersionCommand
import com.dox.application.port.input.ReportUseCase
import com.dox.application.port.input.UpdateReportCommand
import com.dox.application.port.output.ReportPersistencePort
import com.dox.domain.enum.ReportStatus
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.Report
import com.dox.domain.model.ReportVersion
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.UUID

@Service
class ReportServiceImpl(
    private val reportPersistencePort: ReportPersistencePort,
    objectMapper: ObjectMapper,
) : ReportUseCase {
    companion object {
        private const val MAX_VERSIONS = 20
    }

    private val hashMapper: ObjectMapper =
        objectMapper.copy().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)

    private fun computeContentHash(blocks: List<Map<String, Any?>>): String {
        val bytes = hashMapper.writeValueAsBytes(blocks)
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    @Transactional
    override fun create(command: CreateReportCommand): Report =
        reportPersistencePort.save(
            Report(
                customerName = command.customerName,
                customerId = command.customerId,
                formResponseId = command.formResponseId,
                templateId = command.templateId,
                isStructureLocked = command.isStructureLocked,
                blocks = command.blocks,
            ),
        )

    override fun findById(id: UUID): Report =
        reportPersistencePort.findById(id)
            ?: throw ResourceNotFoundException("Relatório", id.toString())

    override fun findAll(pageable: Pageable): Page<Report> = reportPersistencePort.findAll(pageable)

    override fun findByCustomerId(customerId: UUID): List<Report> = reportPersistencePort.findByCustomerId(customerId)

    @Transactional
    override fun update(command: UpdateReportCommand): Report {
        val existing =
            reportPersistencePort.findById(command.id)
                ?: throw ResourceNotFoundException("Relatório", command.id.toString())

        if (existing.status == ReportStatus.FINALIZADO) {
            throw BusinessException("Relatório finalizado é imutável e não pode ser alterado.")
        }

        if (command.status != null && command.status != existing.status) {
            validateStatusTransition(existing.status, command.status)
        }

        val targetStatus = command.status ?: existing.status
        val targetBlocks = command.blocks ?: existing.blocks
        val finalizing = targetStatus == ReportStatus.FINALIZADO && existing.status != ReportStatus.FINALIZADO

        return reportPersistencePort.save(
            existing.copy(
                status = targetStatus,
                customerName = command.customerName ?: existing.customerName,
                blocks = targetBlocks,
                finalizedAt = if (finalizing) LocalDateTime.now() else existing.finalizedAt,
                contentHash = if (finalizing) computeContentHash(targetBlocks) else existing.contentHash,
            ),
        )
    }

    private fun validateStatusTransition(
        current: ReportStatus,
        target: ReportStatus,
    ) {
        val allowed =
            when (current) {
                ReportStatus.RASCUNHO -> setOf(ReportStatus.EM_REVISAO, ReportStatus.FINALIZADO)
                ReportStatus.EM_REVISAO -> setOf(ReportStatus.FINALIZADO, ReportStatus.RASCUNHO)
                ReportStatus.FINALIZADO -> emptySet()
            }
        if (target !in allowed) {
            throw BusinessException("Transição de status inválida: ${current.name} → ${target.name}")
        }
    }

    @Transactional
    override fun delete(id: UUID) {
        val report =
            reportPersistencePort.findById(id)
                ?: throw ResourceNotFoundException("Relatório", id.toString())
        if (report.status == ReportStatus.FINALIZADO) {
            throw BusinessException("Relatório finalizado não pode ser excluído.")
        }
        reportPersistencePort.softDelete(id)
    }

    override fun getExportData(id: UUID): Report {
        val report =
            reportPersistencePort.findById(id)
                ?: throw ResourceNotFoundException("Relatório", id.toString())

        if (report.status != ReportStatus.FINALIZADO) {
            throw BusinessException("Relatório precisa estar finalizado para exportação. Status atual: ${report.status.name}")
        }

        return report
    }

    override fun getVersions(reportId: UUID): List<ReportVersion> = reportPersistencePort.findVersionsByReportId(reportId)

    @Transactional
    override fun createVersion(command: CreateVersionCommand): ReportVersion {
        val report =
            reportPersistencePort.findById(command.reportId)
                ?: throw ResourceNotFoundException("Relatório", command.reportId.toString())

        if (report.status == ReportStatus.FINALIZADO) {
            throw BusinessException("Relatório finalizado não permite criação de novas versões.")
        }

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
                type = command.type,
            ),
        )
    }
}
