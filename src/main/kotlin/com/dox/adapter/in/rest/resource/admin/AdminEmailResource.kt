package com.dox.adapter.`in`.rest.resource.admin

import com.dox.adapter.`in`.rest.dto.email.AdminEmailTestRequest
import com.dox.adapter.`in`.rest.dto.email.AdminPagedEmailLogResponse
import com.dox.adapter.`in`.rest.dto.email.EmailLogResponse
import com.dox.adapter.`in`.rest.resource.BaseResource
import com.dox.domain.email.EmailLogStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Tag(name = "Admin · Email", description = "Envio de email de teste e auditoria do email_log")
@RequestMapping("/admin/email")
interface AdminEmailResource : BaseResource {
    @Operation(summary = "Envia um email de teste usando o template informado")
    @PostMapping("/test")
    fun sendTest(
        @Valid @RequestBody request: AdminEmailTestRequest,
    ): ResponseEntity<EmailLogResponse>

    @Operation(summary = "Lista logs de email com filtros opcionais")
    @GetMapping("/log")
    fun listLog(
        @RequestParam(required = false) templateId: String?,
        @RequestParam(required = false) status: EmailLogStatus?,
        @RequestParam(required = false) recipientEmail: String?,
        @RequestParam(required = false) tenantId: UUID?,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "20") pageSize: Int,
    ): ResponseEntity<AdminPagedEmailLogResponse>
}
