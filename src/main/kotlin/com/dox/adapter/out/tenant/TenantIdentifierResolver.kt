package com.dox.adapter.out.tenant

import com.dox.shared.TenancyConstant
import com.dox.shared.TenantContext
import org.hibernate.cfg.AvailableSettings
import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.stereotype.Component

@Component
class TenantIdentifierResolver : CurrentTenantIdentifierResolver<String>, HibernatePropertiesCustomizer {
    override fun resolveCurrentTenantIdentifier(): String = TenantContext.getTenantId() ?: TenancyConstant.PUBLIC_SCHEMA

    override fun validateExistingCurrentSessions(): Boolean = true

    override fun customize(hibernateProperties: MutableMap<String, Any>) {
        hibernateProperties[AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER] = this
    }
}
