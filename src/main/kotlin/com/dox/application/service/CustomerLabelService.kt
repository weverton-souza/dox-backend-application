package com.dox.application.service

import com.dox.application.port.output.ProfessionalSettingsPersistencePort
import com.dox.domain.enum.CustomerLabels
import com.dox.domain.enum.Vertical
import com.dox.shared.TenantContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CustomerLabelService(
    private val professionalSettingsPersistencePort: ProfessionalSettingsPersistencePort,
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    fun resolveForTenant(
        tenantId: UUID,
        vertical: Vertical,
    ): String {
        val override =
            TenantContext.withTenantContext(tenantId) {
                professionalSettingsPersistencePort.find()?.customerLabelOverride
            }
        return CustomerLabels.resolve(vertical, override)
    }
}
