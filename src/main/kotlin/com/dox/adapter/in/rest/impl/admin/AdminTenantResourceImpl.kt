package com.dox.adapter.`in`.rest.impl.admin

import com.dox.adapter.`in`.rest.dto.admin.AdminBundleInfo
import com.dox.adapter.`in`.rest.dto.admin.AdminPagedResponse
import com.dox.adapter.`in`.rest.dto.admin.AdminPaymentInfo
import com.dox.adapter.`in`.rest.dto.admin.AdminSubscriptionInfo
import com.dox.adapter.`in`.rest.dto.admin.AdminTenantDetailResponse
import com.dox.adapter.`in`.rest.dto.admin.AdminTenantListItem
import com.dox.adapter.`in`.rest.dto.admin.AdminTenantModuleInfo
import com.dox.adapter.`in`.rest.dto.admin.AdminTenantOwner
import com.dox.adapter.`in`.rest.dto.admin.AdminTenantSummary
import com.dox.adapter.`in`.rest.dto.admin.ExtendTrialRequest
import com.dox.adapter.`in`.rest.dto.admin.GrantModuleRequest
import com.dox.adapter.`in`.rest.resource.admin.AdminTenantResource
import com.dox.application.port.input.AdminTenantActionUseCase
import com.dox.application.port.input.AdminTenantDetailResult
import com.dox.application.port.input.AdminTenantUseCase
import com.dox.application.port.input.ExtendTrialCommand
import com.dox.application.port.input.GrantModuleCommand
import com.dox.application.port.input.TenantWithSubscription
import com.dox.domain.billing.Bundle
import com.dox.domain.billing.Payment
import com.dox.domain.billing.Subscription
import com.dox.domain.billing.TenantModule
import com.dox.domain.model.Tenant
import com.dox.domain.model.User
import com.dox.shared.ContextHolder
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class AdminTenantResourceImpl(
    private val adminTenantUseCase: AdminTenantUseCase,
    private val adminTenantActionUseCase: AdminTenantActionUseCase,
) : AdminTenantResource {
    companion object {
        private const val MAX_PAGE_SIZE = 100
    }

    override fun list(
        search: String?,
        page: Int,
        size: Int,
    ): ResponseEntity<AdminPagedResponse<AdminTenantListItem>> {
        val pageable =
            PageRequest.of(
                page.coerceAtLeast(0),
                size.coerceIn(1, MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "createdAt"),
            )
        val result = adminTenantUseCase.listTenants(search, pageable)
        return responseEntity(
            AdminPagedResponse(
                content = result.content.map { it.toListItem() },
                page = result.number,
                size = result.size,
                totalElements = result.totalElements,
                totalPages = result.totalPages,
            ),
        )
    }

    override fun detail(id: UUID): ResponseEntity<AdminTenantDetailResponse> = responseEntity(adminTenantUseCase.getDetail(id).toResponse())

    override fun grantModule(
        id: UUID,
        request: GrantModuleRequest,
    ): ResponseEntity<AdminTenantModuleInfo> {
        val actorAdminId = ContextHolder.getUserIdOrThrow()
        val granted =
            adminTenantActionUseCase.grantModule(
                tenantId = id,
                command = GrantModuleCommand(moduleId = request.moduleId, expiresAt = request.expiresAt, notes = request.notes),
                actorAdminId = actorAdminId,
            )
        return responseEntity(granted.toInfo())
    }

    override fun extendTrial(
        id: UUID,
        request: ExtendTrialRequest,
    ): ResponseEntity<AdminSubscriptionInfo> {
        val actorAdminId = ContextHolder.getUserIdOrThrow()
        val updated =
            adminTenantActionUseCase.extendTrial(
                tenantId = id,
                command = ExtendTrialCommand(days = request.days, notes = request.notes),
                actorAdminId = actorAdminId,
            )
        return responseEntity(updated.toInfo())
    }

    private fun TenantWithSubscription.toListItem() =
        AdminTenantListItem(
            id = tenant.id,
            name = tenant.name,
            vertical = tenant.vertical,
            type = tenant.type,
            subscriptionStatus = subscription?.status,
            mrrCents = subscription?.valueCents,
            createdAt = tenant.createdAt,
        )

    private fun AdminTenantDetailResult.toResponse() =
        AdminTenantDetailResponse(
            tenant = tenant.toSummary(),
            owner = owner?.toOwner(),
            subscription = subscription?.toInfo(),
            bundle = bundle?.toInfo(),
            modules = modules.map { it.toInfo() },
            recentPayments = recentPayments.map { it.toInfo() },
        )

    private fun Tenant.toSummary() =
        AdminTenantSummary(
            id = id,
            name = name,
            vertical = vertical,
            type = type,
            schemaName = schemaName,
            createdAt = createdAt,
        )

    private fun User.toOwner() =
        AdminTenantOwner(
            userId = id,
            email = email,
            name = name,
        )

    private fun Subscription.toInfo() =
        AdminSubscriptionInfo(
            id = id,
            status = status,
            billingCycle = billingCycle,
            billingType = billingType,
            valueCents = valueCents,
            currentPeriodStart = currentPeriodStart,
            currentPeriodEnd = currentPeriodEnd,
            nextDueDate = nextDueDate,
            trialEnd = trialEnd,
            canceledAt = canceledAt,
        )

    private fun Bundle.toInfo() =
        AdminBundleInfo(
            id = id,
            name = name,
            priceMonthlyCents = priceMonthlyCents,
            priceYearlyCents = priceYearlyCents,
            seatsIncluded = seatsIncluded,
            trackingSlotsIncluded = trackingSlotsIncluded,
        )

    private fun TenantModule.toInfo() =
        AdminTenantModuleInfo(
            moduleId = moduleId,
            status = status,
            source = source,
            sourceId = sourceId,
            activatedAt = activatedAt,
            expiresAt = expiresAt,
            priceLocked = priceLocked,
            finalPriceCents = finalPriceCents,
        )

    private fun Payment.toInfo() =
        AdminPaymentInfo(
            id = id,
            amountCents = amountCents,
            status = status,
            billingType = billingType,
            dueDate = dueDate,
            paidAt = paidAt,
            invoiceUrl = invoiceUrl,
        )
}
