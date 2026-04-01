package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.ai.AiGenerationSourceResponse
import com.dox.adapter.`in`.rest.dto.ai.AiQuotaResponse
import com.dox.adapter.`in`.rest.dto.ai.AiStatusResponse
import com.dox.adapter.`in`.rest.dto.ai.AiUsageDetailResponse
import com.dox.adapter.`in`.rest.dto.ai.AiUsageSummaryResponse
import com.dox.adapter.`in`.rest.dto.ai.GenerateFullReportRequest
import com.dox.adapter.`in`.rest.dto.ai.GenerateSectionRequest
import com.dox.adapter.`in`.rest.dto.ai.GenerateSectionResponse
import com.dox.adapter.`in`.rest.dto.ai.RegenerateSectionRequest
import com.dox.adapter.`in`.rest.dto.ai.RegenerationInfoResponse
import com.dox.adapter.`in`.rest.dto.ai.ReviewTextRequest
import com.dox.adapter.`in`.rest.dto.ai.ReviewTextResponse
import com.dox.adapter.`in`.rest.dto.ai.UpdateAiQuotaRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.UUID

@Tag(name = "IA", description = "Geração de laudos por IA e controle de consumo")
interface AiResource : BaseResource {
    @Operation(summary = "Gerar seção do laudo com IA")
    @PostMapping("/reports/{id}/generate-section")
    fun generateSection(
        @PathVariable id: UUID,
        @Valid @RequestBody request: GenerateSectionRequest,
    ): ResponseEntity<GenerateSectionResponse>

    @Operation(summary = "Regerar seção do laudo com IA")
    @PostMapping("/reports/{id}/regenerate-section")
    fun regenerateSection(
        @PathVariable id: UUID,
        @Valid @RequestBody request: RegenerateSectionRequest,
    ): ResponseEntity<GenerateSectionResponse>

    @Operation(summary = "Gerar laudo completo com IA (SSE)")
    @PostMapping("/reports/{id}/generate-all", produces = ["text/event-stream"])
    fun generateFullReport(
        @PathVariable id: UUID,
        @Valid @RequestBody request: GenerateFullReportRequest,
    ): SseEmitter

    @Operation(summary = "Resumo de consumo de IA do mês")
    @GetMapping("/ai/usage/summary")
    fun getUsageSummary(
        @RequestParam month: Int,
        @RequestParam year: Int,
    ): ResponseEntity<AiUsageSummaryResponse>

    @Operation(summary = "Histórico detalhado de uso de IA")
    @GetMapping("/ai/usage/history")
    fun getUsageHistory(
        @RequestParam month: Int,
        @RequestParam year: Int,
    ): ResponseEntity<List<AiUsageDetailResponse>>

    @Operation(summary = "Histórico de uso de IA por relatório")
    @GetMapping("/ai/usage/report/{reportId}")
    fun getUsageByReport(
        @PathVariable reportId: UUID,
    ): ResponseEntity<List<AiUsageDetailResponse>>

    @Operation(summary = "Quota de IA do workspace")
    @GetMapping("/ai/quota")
    fun getQuota(): ResponseEntity<AiQuotaResponse>

    @Operation(summary = "Atualizar quota de IA do workspace")
    @PutMapping("/ai/quota")
    fun updateQuota(
        @Valid @RequestBody request: UpdateAiQuotaRequest,
    ): ResponseEntity<AiQuotaResponse>

    @Operation(summary = "Status do serviço de IA")
    @GetMapping("/ai/status")
    fun getAiStatus(): ResponseEntity<AiStatusResponse>

    @Operation(summary = "Revisar texto existente com Assistente")
    @PostMapping("/reports/{id}/review-text")
    fun reviewText(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ReviewTextRequest,
    ): ResponseEntity<ReviewTextResponse>

    @Operation(summary = "Fontes usadas nas gerações do relatório")
    @GetMapping("/reports/{reportId}/generation-sources")
    fun getGenerationSources(
        @PathVariable reportId: UUID,
    ): ResponseEntity<List<AiGenerationSourceResponse>>

    @Operation(summary = "Info de regenerações disponíveis para o relatório")
    @GetMapping("/reports/{reportId}/regeneration-info")
    fun getRegenerationInfo(
        @PathVariable reportId: UUID,
    ): ResponseEntity<RegenerationInfoResponse>
}
