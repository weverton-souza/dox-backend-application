package com.dox.adapter.`in`.rest.resource.admin

import com.dox.adapter.`in`.rest.dto.admin.AdminPagedResponse
import com.dox.adapter.`in`.rest.dto.admin.AdminPromotionResponse
import com.dox.adapter.`in`.rest.dto.admin.AdminPromotionStatsResponse
import com.dox.adapter.`in`.rest.dto.admin.CreatePromotionRequest
import com.dox.adapter.`in`.rest.dto.admin.UpdatePromotionRequest
import com.dox.adapter.`in`.rest.resource.BaseResource
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Tag(name = "Admin · Promoções", description = "Gestão de promoções (catálogo) no backoffice")
@RequestMapping("/admin/promotions")
interface AdminPromotionResource : BaseResource {
    @Operation(summary = "Lista paginada de promoções")
    @GetMapping
    fun list(
        @RequestParam(defaultValue = "false") includeArchived: Boolean,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<AdminPagedResponse<AdminPromotionResponse>>

    @Operation(summary = "Cria nova promoção")
    @PostMapping
    fun create(
        @Valid @RequestBody request: CreatePromotionRequest,
    ): ResponseEntity<AdminPromotionResponse>

    @Operation(summary = "Atualiza campos editáveis da promoção")
    @PatchMapping("/{promotionId}")
    fun update(
        @PathVariable promotionId: UUID,
        @Valid @RequestBody request: UpdatePromotionRequest,
    ): ResponseEntity<AdminPromotionResponse>

    @Operation(summary = "Arquiva promoção (não permite mais aplicações)")
    @PostMapping("/{promotionId}/archive")
    fun archive(
        @PathVariable promotionId: UUID,
    ): ResponseEntity<AdminPromotionResponse>

    @Operation(summary = "Estatísticas de uso da promoção")
    @GetMapping("/{promotionId}/stats")
    fun stats(
        @PathVariable promotionId: UUID,
    ): ResponseEntity<AdminPromotionStatsResponse>
}
