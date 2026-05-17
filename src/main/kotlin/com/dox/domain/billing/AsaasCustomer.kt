package com.dox.domain.billing

import java.time.LocalDateTime
import java.util.UUID

data class AsaasCustomer(
    val id: UUID = UUID.randomUUID(),
    val tenantId: UUID,
    val asaasCustomerId: String,
    val cpfCnpj: String,
    val email: String? = null,
    val name: String,
    val billingMobilePhone: String? = null,
    val billingPostalCode: String? = null,
    val billingAddress: String? = null,
    val billingAddressNumber: String? = null,
    val billingComplement: String? = null,
    val billingProvince: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
