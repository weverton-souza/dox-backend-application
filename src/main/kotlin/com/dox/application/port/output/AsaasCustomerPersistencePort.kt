package com.dox.application.port.output

import com.dox.domain.billing.AsaasCustomer
import java.util.UUID

interface AsaasCustomerPersistencePort {
    fun findByTenantId(tenantId: UUID): AsaasCustomer?

    fun save(customer: AsaasCustomer): AsaasCustomer
}
