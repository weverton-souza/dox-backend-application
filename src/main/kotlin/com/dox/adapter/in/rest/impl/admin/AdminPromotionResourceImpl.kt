package com.dox.adapter.`in`.rest.impl.admin

import com.dox.adapter.`in`.rest.dto.admin.AdminPagedResponse
import com.dox.adapter.`in`.rest.dto.admin.AdminPromotionResponse
import com.dox.adapter.`in`.rest.dto.admin.AdminPromotionStatsResponse
import com.dox.adapter.`in`.rest.dto.admin.CreatePromotionRequest
import com.dox.adapter.`in`.rest.dto.admin.UpdatePromotionRequest
import com.dox.adapter.`in`.rest.resource.admin.AdminPromotionResource
import com.dox.application.port.input.AdminPromotionUseCase
import com.dox.application.port.input.CreatePromotionCommand
import com.dox.application.port.input.PromotionStats
import com.dox.application.port.input.UpdatePromotionCommand
import com.dox.domain.billing.Promotion
import com.dox.shared.ContextHolder
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class AdminPromotionResourceImpl(
    private val adminPromotionUseCase: AdminPromotionUseCase,
) : AdminPromotionResource {
    companion object {
        private const val MAX_PAGE_SIZE = 100
    }

    override fun list(
        includeArchived: Boolean,
        page: Int,
        size: Int,
    ): ResponseEntity<AdminPagedResponse<AdminPromotionResponse>> {
        val pageable =
            PageRequest.of(
                page.coerceAtLeast(0),
                size.coerceIn(1, MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "createdAt"),
            )
        val result = adminPromotionUseCase.listPromotions(includeArchived, pageable)
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

    override fun create(request: CreatePromotionRequest): ResponseEntity<AdminPromotionResponse> {
        val actorAdminId = ContextHolder.getUserIdOrThrow()
        val saved =
            adminPromotionUseCase.createPromotion(
                CreatePromotionCommand(
                    code = request.code,
                    name = request.name,
                    type = request.type,
                    discountType = request.discountType,
                    discountValue = request.discountValue,
                    durationType = request.durationType,
                    durationMonths = request.durationMonths,
                    maxRedemptions = request.maxRedemptions,
                    validFrom = request.validFrom,
                    validUntil = request.validUntil,
                    appliesTo = request.appliesTo,
                    appliesToModules = request.appliesToModules,
                    appliesToVerticals = request.appliesToVerticals,
                    appliesToSignupAfter = request.appliesToSignupAfter,
                    appliesToSignupBefore = request.appliesToSignupBefore,
                    stackableWith = request.stackableWith,
                    skipProration = request.skipProration,
                    requiresApproval = request.requiresApproval,
                    autoApplyEvent = request.autoApplyEvent,
                ),
                actorAdminId,
            )
        return responseEntity(saved.toResponse(), HttpStatus.CREATED)
    }

    override fun update(
        promotionId: UUID,
        request: UpdatePromotionRequest,
    ): ResponseEntity<AdminPromotionResponse> {
        val actorAdminId = ContextHolder.getUserIdOrThrow()
        val saved =
            adminPromotionUseCase.updatePromotion(
                promotionId,
                UpdatePromotionCommand(
                    name = request.name,
                    maxRedemptions = request.maxRedemptions,
                    validFrom = request.validFrom,
                    validUntil = request.validUntil,
                    appliesToModules = request.appliesToModules,
                    appliesToVerticals = request.appliesToVerticals,
                    appliesToSignupAfter = request.appliesToSignupAfter,
                    appliesToSignupBefore = request.appliesToSignupBefore,
                    stackableWith = request.stackableWith,
                    requiresApproval = request.requiresApproval,
                    autoApplyEvent = request.autoApplyEvent,
                ),
                actorAdminId,
            )
        return responseEntity(saved.toResponse())
    }

    override fun archive(promotionId: UUID): ResponseEntity<AdminPromotionResponse> {
        val actorAdminId = ContextHolder.getUserIdOrThrow()
        val saved = adminPromotionUseCase.archivePromotion(promotionId, actorAdminId)
        return responseEntity(saved.toResponse())
    }

    override fun stats(promotionId: UUID): ResponseEntity<AdminPromotionStatsResponse> {
        val stats = adminPromotionUseCase.getStats(promotionId)
        return responseEntity(stats.toResponse())
    }

    private fun Promotion.toResponse() =
        AdminPromotionResponse(
            id = id,
            code = code,
            name = name,
            type = type,
            discountType = discountType,
            discountValue = discountValue,
            durationType = durationType,
            durationMonths = durationMonths,
            maxRedemptions = maxRedemptions,
            currentRedemptions = currentRedemptions,
            validFrom = validFrom,
            validUntil = validUntil,
            appliesTo = appliesTo,
            appliesToModules = appliesToModules,
            appliesToVerticals = appliesToVerticals,
            appliesToSignupAfter = appliesToSignupAfter,
            appliesToSignupBefore = appliesToSignupBefore,
            stackableWith = stackableWith,
            skipProration = skipProration,
            requiresApproval = requiresApproval,
            autoApplyEvent = autoApplyEvent,
            createdAt = createdAt,
            createdByUserId = createdByUserId,
            archivedAt = archivedAt,
        )

    private fun PromotionStats.toResponse() =
        AdminPromotionStatsResponse(
            promotionId = promotionId,
            currentRedemptions = currentRedemptions,
            maxRedemptions = maxRedemptions,
            activeTenantIds = activeTenantIds,
        )
}
