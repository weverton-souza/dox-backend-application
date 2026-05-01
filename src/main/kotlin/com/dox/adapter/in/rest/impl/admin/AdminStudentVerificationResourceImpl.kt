package com.dox.adapter.`in`.rest.impl.admin

import com.dox.adapter.`in`.rest.dto.admin.AdminPagedResponse
import com.dox.adapter.`in`.rest.dto.admin.AdminStudentVerificationResponse
import com.dox.adapter.`in`.rest.dto.admin.ApproveStudentVerificationRequest
import com.dox.adapter.`in`.rest.dto.admin.CreateStudentVerificationRequest
import com.dox.adapter.`in`.rest.dto.admin.RejectStudentVerificationRequest
import com.dox.adapter.`in`.rest.resource.admin.AdminStudentVerificationResource
import com.dox.application.port.input.AdminStudentVerificationUseCase
import com.dox.application.port.input.CreateStudentVerificationCommand
import com.dox.domain.billing.StudentVerification
import com.dox.domain.billing.StudentVerificationStatus
import com.dox.shared.ContextHolder
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class AdminStudentVerificationResourceImpl(
    private val adminStudentVerificationUseCase: AdminStudentVerificationUseCase,
) : AdminStudentVerificationResource {
    companion object {
        private const val MAX_PAGE_SIZE = 100
    }

    override fun list(
        status: StudentVerificationStatus?,
        page: Int,
        size: Int,
    ): ResponseEntity<AdminPagedResponse<AdminStudentVerificationResponse>> {
        val pageable =
            PageRequest.of(
                page.coerceAtLeast(0),
                size.coerceIn(1, MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.ASC, "createdAt"),
            )
        val result = adminStudentVerificationUseCase.list(status, pageable)
        return responseEntity(
            AdminPagedResponse(
                content = result.content.map { it.toResponse() },
                page = result.number,
                size = result.size,
                totalElements = result.totalElements,
                totalPages = result.totalPages,
            ),
        )
    }

    override fun getById(id: UUID): ResponseEntity<AdminStudentVerificationResponse> = responseEntity(adminStudentVerificationUseCase.getById(id).toResponse())

    override fun createManual(request: CreateStudentVerificationRequest): ResponseEntity<AdminStudentVerificationResponse> {
        val actorAdminId = ContextHolder.getUserIdOrThrow()
        val saved =
            adminStudentVerificationUseCase.createManual(
                CreateStudentVerificationCommand(
                    tenantId = request.tenantId,
                    userId = request.userId,
                    documentUrl = request.documentUrl,
                    institution = request.institution,
                    course = request.course,
                    expectedGraduation = request.expectedGraduation,
                    notes = request.notes,
                ),
                actorAdminId,
            )
        return responseEntity(saved.toResponse(), HttpStatus.CREATED)
    }

    override fun approve(
        id: UUID,
        request: ApproveStudentVerificationRequest,
    ): ResponseEntity<AdminStudentVerificationResponse> {
        val actorAdminId = ContextHolder.getUserIdOrThrow()
        val saved = adminStudentVerificationUseCase.approve(id, request.notes, actorAdminId)
        return responseEntity(saved.toResponse())
    }

    override fun reject(
        id: UUID,
        request: RejectStudentVerificationRequest,
    ): ResponseEntity<AdminStudentVerificationResponse> {
        val actorAdminId = ContextHolder.getUserIdOrThrow()
        val saved = adminStudentVerificationUseCase.reject(id, request.rejectionReason, actorAdminId)
        return responseEntity(saved.toResponse())
    }

    private fun StudentVerification.toResponse() =
        AdminStudentVerificationResponse(
            id = id,
            tenantId = tenantId,
            userId = userId,
            documentUrl = documentUrl,
            institution = institution,
            course = course,
            expectedGraduation = expectedGraduation,
            status = status,
            reviewedByAdminId = reviewedByAdminId,
            reviewedAt = reviewedAt,
            notes = notes,
            rejectionReason = rejectionReason,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
