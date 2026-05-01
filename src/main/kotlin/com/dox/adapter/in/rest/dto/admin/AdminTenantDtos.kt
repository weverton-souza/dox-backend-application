package com.dox.adapter.`in`.rest.dto.admin

import com.dox.domain.billing.BillingCycle
import com.dox.domain.billing.BillingType
import com.dox.domain.billing.ModuleSource
import com.dox.domain.billing.ModuleStatus
import com.dox.domain.billing.SubscriptionStatus
import com.dox.domain.enum.TenantType
import com.dox.domain.enum.Vertical
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class GrantModuleRequest(
    @field:NotBlank(message = "Módulo é obrigatório")
    val moduleId: String,
    val expiresAt: LocalDateTime? = null,
    @field:Size(max = 500, message = "Notas devem ter no máximo 500 caracteres")
    val notes: String? = null,
)

data class ExtendTrialRequest(
    @field:Min(value = 1, message = "Dias deve ser positivo")
    val days: Int,
    @field:Size(max = 500, message = "Notas devem ter no máximo 500 caracteres")
    val notes: String? = null,
)

data class AdminPagedResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

data class AdminTenantListItem(
    val id: UUID,
    val name: String,
    val vertical: Vertical,
    val type: TenantType,
    val subscriptionStatus: SubscriptionStatus?,
    val mrrCents: Int?,
    val createdAt: LocalDateTime?,
)

data class AdminTenantDetailResponse(
    val tenant: AdminTenantSummary,
    val owner: AdminTenantOwner?,
    val subscription: AdminSubscriptionInfo?,
    val bundle: AdminBundleInfo?,
    val modules: List<AdminTenantModuleInfo>,
    val recentPayments: List<AdminPaymentInfo>,
)

data class AdminTenantSummary(
    val id: UUID,
    val name: String,
    val vertical: Vertical,
    val type: TenantType,
    val schemaName: String,
    val createdAt: LocalDateTime?,
)

data class AdminTenantOwner(
    val userId: UUID,
    val email: String,
    val name: String,
)

data class AdminSubscriptionInfo(
    val id: UUID,
    val status: SubscriptionStatus,
    val billingCycle: BillingCycle,
    val billingType: BillingType,
    val valueCents: Int,
    val currentPeriodStart: LocalDateTime?,
    val currentPeriodEnd: LocalDateTime?,
    val nextDueDate: LocalDate?,
    val trialEnd: LocalDateTime?,
    val canceledAt: LocalDateTime?,
)

data class AdminBundleInfo(
    val id: String,
    val name: String,
    val priceMonthlyCents: Int,
    val priceYearlyCents: Int,
    val seatsIncluded: Int,
    val trackingSlotsIncluded: Int,
)

data class AdminTenantModuleInfo(
    val moduleId: String,
    val status: ModuleStatus,
    val source: ModuleSource,
    val sourceId: String?,
    val activatedAt: LocalDateTime,
    val expiresAt: LocalDateTime?,
    val priceLocked: Boolean,
    val finalPriceCents: Int,
)

data class AdminPaymentInfo(
    val id: UUID,
    val amountCents: Int,
    val status: String,
    val billingType: BillingType,
    val dueDate: LocalDate,
    val paidAt: LocalDateTime?,
    val invoiceUrl: String?,
)
