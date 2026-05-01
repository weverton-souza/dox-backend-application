package com.dox.application.port.input

import com.dox.domain.billing.Subscription
import com.dox.domain.billing.TenantModule
import java.time.LocalDateTime
import java.util.UUID

data class GrantModuleCommand(
    val moduleId: String,
    val expiresAt: LocalDateTime?,
    val notes: String?,
)

data class ExtendTrialCommand(
    val days: Int,
    val notes: String?,
)

interface AdminTenantActionUseCase {
    fun grantModule(
        tenantId: UUID,
        command: GrantModuleCommand,
        actorAdminId: UUID,
    ): TenantModule

    fun extendTrial(
        tenantId: UUID,
        command: ExtendTrialCommand,
        actorAdminId: UUID,
    ): Subscription
}
