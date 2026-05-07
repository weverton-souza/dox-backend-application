package com.dox.application.service

import com.dox.domain.enum.CustomerLabels
import com.dox.domain.enum.Vertical
import com.dox.shared.TenantContext
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CustomerLabelService(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun resolveForTenant(
        tenantId: UUID,
        vertical: Vertical,
    ): String {
        val schema = TenantContext.convertToSchemaName(tenantId.toString())
        val override =
            try {
                jdbcTemplate.queryForObject(
                    "SELECT customer_label_override FROM \"$schema\".professional_settings ORDER BY created_at LIMIT 1",
                    String::class.java,
                )
            } catch (e: EmptyResultDataAccessException) {
                null
            }
        return CustomerLabels.resolve(vertical, override)
    }
}
