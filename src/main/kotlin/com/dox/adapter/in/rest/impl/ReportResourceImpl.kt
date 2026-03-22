package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.report.ReportRequest
import com.dox.adapter.`in`.rest.dto.report.ReportResponse
import com.dox.adapter.`in`.rest.dto.report.ReportVersionRequest
import com.dox.adapter.`in`.rest.dto.report.ReportVersionResponse
import com.dox.adapter.`in`.rest.resource.ReportResource
import com.dox.application.port.input.CreateReportCommand
import com.dox.application.port.input.CreateVersionCommand
import com.dox.application.port.input.ReportUseCase
import com.dox.application.port.input.UpdateReportCommand
import com.dox.domain.model.Report
import com.dox.domain.model.ReportVersion
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class ReportResourceImpl(
    private val reportUseCase: ReportUseCase
) : ReportResource {

    override fun findAll(parameters: Map<String, Any>): ResponseEntity<Page<ReportResponse>> =
        responseEntity(reportUseCase.findAll(retrievePageableParameter(parameters)).map { it.toResponse() })

    override fun create(request: ReportRequest): ResponseEntity<ReportResponse> =
        responseEntity(
            reportUseCase.create(
                CreateReportCommand(
                    customerName = request.customerName,
                    customerId = request.customerId,
                    formResponseId = request.formResponseId,
                    blocks = request.blocks
                )
            ).toResponse(),
            HttpStatus.CREATED
        )

    override fun findById(id: UUID): ResponseEntity<ReportResponse> =
        responseEntity(reportUseCase.findById(id).toResponse())

    override fun update(id: UUID, request: ReportRequest): ResponseEntity<ReportResponse> =
        responseEntity(
            reportUseCase.update(
                UpdateReportCommand(
                    id = id,
                    status = request.status,
                    customerName = request.customerName,
                    blocks = request.blocks
                )
            ).toResponse()
        )

    override fun delete(id: UUID): ResponseEntity<Void> {
        reportUseCase.delete(id)
        return noContent()
    }

    override fun findByCustomerId(customerId: UUID): ResponseEntity<List<ReportResponse>> =
        responseEntity(reportUseCase.findByCustomerId(customerId).map { it.toResponse() })

    override fun getVersions(id: UUID): ResponseEntity<List<ReportVersionResponse>> =
        responseEntity(reportUseCase.getVersions(id).map { it.toResponse() })

    override fun createVersion(id: UUID, request: ReportVersionRequest): ResponseEntity<ReportVersionResponse> =
        responseEntity(
            reportUseCase.createVersion(
                CreateVersionCommand(reportId = id, description = request.description, type = request.type)
            ).toResponse(),
            HttpStatus.CREATED
        )

    private fun Report.toResponse() = ReportResponse(
        id, status, customerName, customerId, formResponseId, blocks, createdAt, updatedAt
    )

    private fun ReportVersion.toResponse() = ReportVersionResponse(
        id, reportId, status, description, customerName, blocks, type, createdAt
    )
}
