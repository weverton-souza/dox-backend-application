package com.dox.application.port.output

import com.dox.domain.billing.NfseInvoice
import java.util.UUID

interface NfseInvoicePersistencePort {
    fun findByTenantId(tenantId: UUID): List<NfseInvoice>

    fun findByPaymentId(paymentId: UUID): NfseInvoice?

    fun save(invoice: NfseInvoice): NfseInvoice
}
