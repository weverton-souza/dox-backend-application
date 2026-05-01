package com.dox.application.service

import com.dox.application.port.input.AdminTenantActionUseCase
import com.dox.application.port.input.ExtendTrialCommand
import com.dox.application.port.input.GrantModuleCommand
import com.dox.application.port.input.LockPriceCommand
import com.dox.application.port.input.UnlockPriceCommand
import com.dox.application.port.output.BillingAuditLogPersistencePort
import com.dox.application.port.output.SubscriptionPersistencePort
import com.dox.application.port.output.TenantModulePersistencePort
import com.dox.application.port.output.TenantPersistencePort
import com.dox.domain.billing.BillingAuditAction
import com.dox.domain.billing.BillingAuditLog
import com.dox.domain.billing.Module
import com.dox.domain.billing.ModuleSource
import com.dox.domain.billing.ModuleStatus
import com.dox.domain.billing.Subscription
import com.dox.domain.billing.SubscriptionStatus
import com.dox.domain.billing.TenantModule
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class AdminTenantActionServiceImpl(
    private val tenantPersistencePort: TenantPersistencePort,
    private val tenantModulePersistencePort: TenantModulePersistencePort,
    private val subscriptionPersistencePort: SubscriptionPersistencePort,
    private val billingAuditLogPersistencePort: BillingAuditLogPersistencePort,
) : AdminTenantActionUseCase {
    @Transactional
    override fun grantModule(
        tenantId: UUID,
        command: GrantModuleCommand,
        actorAdminId: UUID,
    ): TenantModule {
        tenantPersistencePort.findById(tenantId)
            ?: throw ResourceNotFoundException("Tenant", tenantId.toString())

        Module.fromId(command.moduleId)
            ?: throw BusinessException("Módulo desconhecido: ${command.moduleId}")

        val existing = tenantModulePersistencePort.findByTenantIdAndModuleId(tenantId, command.moduleId)

        val now = LocalDateTime.now()
        val base =
            existing ?: TenantModule(
                tenantId = tenantId,
                moduleId = command.moduleId,
                status = ModuleStatus.GRANTED,
                source = ModuleSource.GRANT,
                activatedAt = now,
            )
        val granted =
            base.copy(
                status = ModuleStatus.GRANTED,
                source = ModuleSource.GRANT,
                sourceId = actorAdminId.toString(),
                activatedAt = existing?.activatedAt ?: now,
                expiresAt = command.expiresAt,
                graceUntil = null,
                basePriceCents = 0,
                finalPriceCents = 0,
                priceLocked = true,
                priceLockedAt = now,
                canceledAt = null,
                cancelReason = null,
            )

        val saved = tenantModulePersistencePort.save(granted)

        billingAuditLogPersistencePort.save(
            BillingAuditLog(
                tenantId = tenantId,
                actorAdminId = actorAdminId,
                action = BillingAuditAction.GRANT_MODULE,
                beforeState = existing?.toAuditMap(),
                afterState = saved.toAuditMap(),
                notes = command.notes,
            ),
        )

        return saved
    }

    @Transactional
    override fun extendTrial(
        tenantId: UUID,
        command: ExtendTrialCommand,
        actorAdminId: UUID,
    ): Subscription {
        if (command.days <= 0) {
            throw BusinessException("Quantidade de dias deve ser positiva")
        }

        tenantPersistencePort.findById(tenantId)
            ?: throw ResourceNotFoundException("Tenant", tenantId.toString())

        val subscription =
            subscriptionPersistencePort.findByTenantId(tenantId)
                ?: throw ResourceNotFoundException("Subscription do tenant", tenantId.toString())

        if (subscription.status != SubscriptionStatus.TRIAL && subscription.status != SubscriptionStatus.TRIAL_GRACE) {
            throw BusinessException("Subscription precisa estar em TRIAL ou TRIAL_GRACE para estender")
        }

        val newTrialEnd = (subscription.trialEnd ?: LocalDateTime.now()).plusDays(command.days.toLong())
        val updated = subscription.copy(trialEnd = newTrialEnd)
        val saved = subscriptionPersistencePort.save(updated)

        billingAuditLogPersistencePort.save(
            BillingAuditLog(
                tenantId = tenantId,
                actorAdminId = actorAdminId,
                action = BillingAuditAction.EXTEND_TRIAL,
                beforeState = mapOf("trialEnd" to subscription.trialEnd?.toString(), "status" to subscription.status.name),
                afterState = mapOf("trialEnd" to saved.trialEnd?.toString(), "status" to saved.status.name, "extendedDays" to command.days),
                notes = command.notes,
            ),
        )

        return saved
    }

    @Transactional
    override fun lockPrice(
        tenantId: UUID,
        command: LockPriceCommand,
        actorAdminId: UUID,
    ): List<TenantModule> = togglePriceLock(tenantId, locked = true, reason = command.reason, actorAdminId = actorAdminId)

    @Transactional
    override fun unlockPrice(
        tenantId: UUID,
        command: UnlockPriceCommand,
        actorAdminId: UUID,
    ): List<TenantModule> = togglePriceLock(tenantId, locked = false, reason = command.reason, actorAdminId = actorAdminId)

    private fun togglePriceLock(
        tenantId: UUID,
        locked: Boolean,
        reason: String?,
        actorAdminId: UUID,
    ): List<TenantModule> {
        tenantPersistencePort.findById(tenantId)
            ?: throw ResourceNotFoundException("Tenant", tenantId.toString())

        val activeModules =
            tenantModulePersistencePort.findByTenantId(tenantId)
                .filter { it.canceledAt == null }

        if (activeModules.isEmpty()) {
            throw BusinessException("Tenant não possui módulos ativos para alterar bloqueio de preço")
        }

        val now = LocalDateTime.now()
        val before = activeModules.map { it.toAuditMap() }
        val updated =
            activeModules.map { module ->
                tenantModulePersistencePort.save(
                    module.copy(
                        priceLocked = locked,
                        priceLockedAt = if (locked) now else null,
                    ),
                )
            }
        val after = updated.map { it.toAuditMap() }

        billingAuditLogPersistencePort.save(
            BillingAuditLog(
                tenantId = tenantId,
                actorAdminId = actorAdminId,
                action = if (locked) BillingAuditAction.LOCK_PRICE else BillingAuditAction.UNLOCK_PRICE,
                beforeState = mapOf("modules" to before),
                afterState = mapOf("modules" to after),
                notes = reason,
            ),
        )

        return updated
    }

    private fun TenantModule.toAuditMap(): Map<String, Any?> =
        mapOf(
            "moduleId" to moduleId,
            "status" to status.name,
            "source" to source.name,
            "sourceId" to sourceId,
            "expiresAt" to expiresAt?.toString(),
            "finalPriceCents" to finalPriceCents,
            "priceLocked" to priceLocked,
        )
}
