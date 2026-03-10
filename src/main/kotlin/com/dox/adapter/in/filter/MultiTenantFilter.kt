package com.dox.adapter.`in`.filter

import com.dox.adapter.out.tenant.TenantContext
import com.dox.shared.ContextHolder
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class MultiTenantFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val tenantId = ContextHolder.context.tenantId

        if (tenantId != null) {
            val schemaName = TenantContext.convertToSchemaName(tenantId.toString())
            TenantContext.setTenantId(schemaName)
        }

        try {
            filterChain.doFilter(request, response)
        } finally {
            TenantContext.clear()
        }
    }
}
