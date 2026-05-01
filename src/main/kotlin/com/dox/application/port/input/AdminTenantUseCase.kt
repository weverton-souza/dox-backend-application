package com.dox.application.port.input

import com.dox.domain.billing.Bundle
import com.dox.domain.billing.Payment
import com.dox.domain.billing.Subscription
import com.dox.domain.billing.TenantModule
import com.dox.domain.model.Tenant
import com.dox.domain.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

data class TenantWithSubscription(
    val tenant: Tenant,
    val subscription: Subscription?,
)

data class AdminTenantDetailResult(
    val tenant: Tenant,
    val owner: User?,
    val subscription: Subscription?,
    val modules: List<TenantModule>,
    val bundle: Bundle?,
    val recentPayments: List<Payment>,
)

interface AdminTenantUseCase {
    fun listTenants(
        search: String?,
        pageable: Pageable,
    ): Page<TenantWithSubscription>

    fun getDetail(tenantId: UUID): AdminTenantDetailResult
}
