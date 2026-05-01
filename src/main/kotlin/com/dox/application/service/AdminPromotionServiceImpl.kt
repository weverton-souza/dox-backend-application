package com.dox.application.service

import com.dox.application.port.input.AdminPromotionUseCase
import com.dox.application.port.input.CreatePromotionCommand
import com.dox.application.port.input.PromotionStats
import com.dox.application.port.input.UpdatePromotionCommand
import com.dox.application.port.output.BillingAuditLogPersistencePort
import com.dox.application.port.output.PromotionPersistencePort
import com.dox.application.port.output.TenantPromotionPersistencePort
import com.dox.domain.billing.BillingAuditAction
import com.dox.domain.billing.BillingAuditLog
import com.dox.domain.billing.DiscountType
import com.dox.domain.billing.DurationType
import com.dox.domain.billing.Promotion
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class AdminPromotionServiceImpl(
    private val promotionPersistencePort: PromotionPersistencePort,
    private val tenantPromotionPersistencePort: TenantPromotionPersistencePort,
    private val billingAuditLogPersistencePort: BillingAuditLogPersistencePort,
) : AdminPromotionUseCase {
    @Transactional(readOnly = true)
    override fun listPromotions(
        includeArchived: Boolean,
        pageable: Pageable,
    ): Page<Promotion> = promotionPersistencePort.findPaginated(includeArchived, pageable)

    @Transactional
    override fun createPromotion(
        command: CreatePromotionCommand,
        actorAdminId: UUID,
    ): Promotion {
        validateCreate(command)
        command.code?.trim()?.takeIf { it.isNotBlank() }?.let { code ->
            if (promotionPersistencePort.findByCode(code) != null) {
                throw BusinessException("Já existe promoção com o código '$code'")
            }
        }

        val promotion =
            Promotion(
                code = command.code?.trim()?.takeIf { it.isNotBlank() },
                name = command.name,
                type = command.type,
                discountType = command.discountType,
                discountValue = command.discountValue,
                durationType = command.durationType,
                durationMonths = command.durationMonths,
                maxRedemptions = command.maxRedemptions,
                validFrom = command.validFrom,
                validUntil = command.validUntil,
                appliesTo = command.appliesTo,
                appliesToModules = command.appliesToModules,
                appliesToVerticals = command.appliesToVerticals,
                appliesToSignupAfter = command.appliesToSignupAfter,
                appliesToSignupBefore = command.appliesToSignupBefore,
                stackableWith = command.stackableWith,
                skipProration = command.skipProration,
                requiresApproval = command.requiresApproval,
                autoApplyEvent = command.autoApplyEvent,
                createdByUserId = actorAdminId,
            )
        val saved = promotionPersistencePort.save(promotion)

        billingAuditLogPersistencePort.save(
            BillingAuditLog(
                tenantId = null,
                actorAdminId = actorAdminId,
                action = BillingAuditAction.CREATE_PROMOTION,
                beforeState = null,
                afterState = saved.toAuditMap(),
            ),
        )
        return saved
    }

    @Transactional
    override fun updatePromotion(
        promotionId: UUID,
        command: UpdatePromotionCommand,
        actorAdminId: UUID,
    ): Promotion {
        val existing =
            promotionPersistencePort.findById(promotionId)
                ?: throw ResourceNotFoundException("Promotion", promotionId.toString())
        if (existing.isArchived) {
            throw BusinessException("Promoção arquivada não pode ser editada")
        }
        command.maxRedemptions?.let {
            if (it < existing.currentRedemptions) {
                throw BusinessException("Limite de redemptions menor que o uso atual (${existing.currentRedemptions})")
            }
        }

        val updated =
            existing.copy(
                name = command.name ?: existing.name,
                maxRedemptions = command.maxRedemptions ?: existing.maxRedemptions,
                validFrom = command.validFrom ?: existing.validFrom,
                validUntil = command.validUntil ?: existing.validUntil,
                appliesToModules = command.appliesToModules ?: existing.appliesToModules,
                appliesToVerticals = command.appliesToVerticals ?: existing.appliesToVerticals,
                appliesToSignupAfter = command.appliesToSignupAfter ?: existing.appliesToSignupAfter,
                appliesToSignupBefore = command.appliesToSignupBefore ?: existing.appliesToSignupBefore,
                stackableWith = command.stackableWith ?: existing.stackableWith,
                requiresApproval = command.requiresApproval ?: existing.requiresApproval,
                autoApplyEvent = command.autoApplyEvent ?: existing.autoApplyEvent,
            )
        val saved = promotionPersistencePort.save(updated)

        billingAuditLogPersistencePort.save(
            BillingAuditLog(
                tenantId = null,
                actorAdminId = actorAdminId,
                action = BillingAuditAction.UPDATE_PROMOTION,
                beforeState = existing.toAuditMap(),
                afterState = saved.toAuditMap(),
            ),
        )
        return saved
    }

    @Transactional
    override fun archivePromotion(
        promotionId: UUID,
        actorAdminId: UUID,
    ): Promotion {
        val existing =
            promotionPersistencePort.findById(promotionId)
                ?: throw ResourceNotFoundException("Promotion", promotionId.toString())
        if (existing.isArchived) {
            throw BusinessException("Promoção já está arquivada")
        }
        val now = LocalDateTime.now()
        val saved = promotionPersistencePort.save(existing.copy(archivedAt = now))

        billingAuditLogPersistencePort.save(
            BillingAuditLog(
                tenantId = null,
                actorAdminId = actorAdminId,
                action = BillingAuditAction.ARCHIVE_PROMOTION,
                beforeState = existing.toAuditMap(),
                afterState = saved.toAuditMap(),
            ),
        )
        return saved
    }

    @Transactional(readOnly = true)
    override fun getStats(promotionId: UUID): PromotionStats {
        val promotion =
            promotionPersistencePort.findById(promotionId)
                ?: throw ResourceNotFoundException("Promotion", promotionId.toString())
        return PromotionStats(
            promotionId = promotion.id,
            currentRedemptions = promotion.currentRedemptions,
            maxRedemptions = promotion.maxRedemptions,
            activeTenantIds = emptyList(),
        )
    }

    private fun validateCreate(command: CreatePromotionCommand) {
        if (command.discountValue < 0) {
            throw BusinessException("Valor do desconto não pode ser negativo")
        }
        if (command.discountType == DiscountType.PERCENTAGE && command.discountValue > 100) {
            throw BusinessException("Desconto percentual não pode ser maior que 100")
        }
        if (command.durationType == DurationType.FIXED_MONTHS &&
            (command.durationMonths == null || command.durationMonths <= 0)
        ) {
            throw BusinessException("Duração em meses é obrigatória para FIXED_MONTHS")
        }
        if (command.maxRedemptions != null && command.maxRedemptions <= 0) {
            throw BusinessException("Limite de redemptions deve ser positivo")
        }
        if (command.validFrom != null && command.validUntil != null &&
            command.validUntil.isBefore(command.validFrom)
        ) {
            throw BusinessException("Validade final deve ser posterior à inicial")
        }
    }

    private fun Promotion.toAuditMap(): Map<String, Any?> =
        mapOf(
            "id" to id.toString(),
            "code" to code,
            "name" to name,
            "type" to type.name,
            "discountType" to discountType.name,
            "discountValue" to discountValue,
            "durationType" to durationType.name,
            "durationMonths" to durationMonths,
            "maxRedemptions" to maxRedemptions,
            "currentRedemptions" to currentRedemptions,
            "validFrom" to validFrom?.toString(),
            "validUntil" to validUntil?.toString(),
            "appliesTo" to appliesTo.name,
            "appliesToModules" to appliesToModules,
            "appliesToVerticals" to appliesToVerticals,
            "stackableWith" to stackableWith,
            "requiresApproval" to requiresApproval,
            "archivedAt" to archivedAt?.toString(),
        )
}
