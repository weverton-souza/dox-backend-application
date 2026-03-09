package com.dox.adapter.`in`.filter

import com.dox.application.port.output.AuthTokenPort
import com.dox.shared.Context
import com.dox.shared.ContextHolder
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val authTokenPort: AuthTokenPort
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = extractToken(request)

        if (token != null && authTokenPort.validateAccessToken(token)) {
            val userId = authTokenPort.extractUserId(token)
            val email = authTokenPort.extractEmail(token)
            val tenantId = authTokenPort.extractTenantId(token)

            val auth = UsernamePasswordAuthenticationToken(email, null, emptyList())
            SecurityContextHolder.getContext().authentication = auth

            ContextHolder.context = Context(tenantId = tenantId, userId = userId)
        }

        try {
            filterChain.doFilter(request, response)
        } finally {
            ContextHolder.clear()
        }
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val header = request.getHeader("Authorization") ?: return null
        return if (header.startsWith("Bearer ")) header.substring(7) else null
    }
}
