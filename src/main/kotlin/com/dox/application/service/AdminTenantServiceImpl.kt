package com.dox.application.service

import com.dox.application.port.input.AdminTenantDetailResult
import com.dox.application.port.input.AdminTenantUseCase
import com.dox.application.port.input.TenantWithSubscription
import com.dox.application.port.output.BundlePersistencePort
import com.dox.application.port.output.PaymentPersistencePort
import com.dox.application.port.output.SubscriptionPersistencePort
import com.dox.application.port.output.TenantModulePersistencePort
import com.dox.application.port.output.TenantPersistencePort
import com.dox.application.port.output.UserPersistencePort
import com.dox.domain.billing.ModuleSource
import com.dox.domain.exception.ResourceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AdminTenantServiceImpl(
    private val tenantPersistencePort: TenantPersistencePort,
    private val subscriptionPersistencePort: SubscriptionPersistencePort,
    private val tenantModulePersistencePort: TenantModulePersistencePort,
    private val paymentPersistencePort: PaymentPersistencePort,
    private val userPersistencePort: UserPersistencePort,
    private val bundlePersistencePort: BundlePersistencePort,
) : AdminTenantUseCase {
    companion object {
        private const val RECENT_PAYMENTS_LIMIT = 20
    }

    @Transactional(readOnly = true)
    override fun listTenants(
        search: String?,
        pageable: Pageable,
    ): Page<TenantWithSubscription> =
        tenantPersistencePort.findAllPaginated(search, pageable)
            .map { tenant ->
                TenantWithSubscription(
                    tenant = tenant,
                    subscription = subscriptionPersistencePort.findByTenantId(tenant.id),
                )
            }

    @Transactional(readOnly = true)
    override fun getDetail(tenantId: UUID): AdminTenantDetailResult {
        val tenant =
            tenantPersistencePort.findById(tenantId)
                ?: throw ResourceNotFoundException("Tenant", tenantId.toString())

        val modules = tenantModulePersistencePort.findByTenantId(tenantId)
        val bundleId = modules.firstOrNull { it.source == ModuleSource.BUNDLE }?.sourceId

        return AdminTenantDetailResult(
            tenant = tenant,
            owner = userPersistencePort.findByPersonalTenantId(tenantId),
            subscription = subscriptionPersistencePort.findByTenantId(tenantId),
            modules = modules,
            bundle = bundleId?.let { bundlePersistencePort.findById(it) },
            recentPayments = paymentPersistencePort.findByTenantId(tenantId).take(RECENT_PAYMENTS_LIMIT),
        )
    }
}
