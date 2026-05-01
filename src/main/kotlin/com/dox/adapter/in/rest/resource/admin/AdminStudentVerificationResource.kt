package com.dox.adapter.`in`.rest.resource.admin

import com.dox.adapter.`in`.rest.dto.admin.AdminPagedResponse
import com.dox.adapter.`in`.rest.dto.admin.AdminStudentVerificationResponse
import com.dox.adapter.`in`.rest.dto.admin.ApproveStudentVerificationRequest
import com.dox.adapter.`in`.rest.dto.admin.CreateStudentVerificationRequest
import com.dox.adapter.`in`.rest.dto.admin.RejectStudentVerificationRequest
import com.dox.adapter.`in`.rest.resource.BaseResource
import com.dox.domain.billing.StudentVerificationStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Tag(
    name = "Admin · Verificação Estudante",
    description = "Aprovação manual de verificações de estudante (programa de plano gratuito)",
)
@RequestMapping("/admin/student-verifications")
interface AdminStudentVerificationResource : BaseResource {
    @Operation(summary = "Lista paginada de verificações")
    @GetMapping
    fun list(
        @RequestParam(required = false) status: StudentVerificationStatus?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<AdminPagedResponse<AdminStudentVerificationResponse>>

    @Operation(summary = "Detalhe da verificação")
    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID,
    ): ResponseEntity<AdminStudentVerificationResponse>

    @Operation(summary = "Cadastra verificação manualmente (admin recebe carteirinha por email)")
    @PostMapping("/manual")
    fun createManual(
        @Valid @RequestBody request: CreateStudentVerificationRequest,
    ): ResponseEntity<AdminStudentVerificationResponse>

    @Operation(summary = "Aprova verificação e aplica promoção STUDENT no tenant")
    @PostMapping("/{id}/approve")
    fun approve(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ApproveStudentVerificationRequest,
    ): ResponseEntity<AdminStudentVerificationResponse>

    @Operation(summary = "Rejeita verificação com motivo")
    @PostMapping("/{id}/reject")
    fun reject(
        @PathVariable id: UUID,
        @Valid @RequestBody request: RejectStudentVerificationRequest,
    ): ResponseEntity<AdminStudentVerificationResponse>
}
