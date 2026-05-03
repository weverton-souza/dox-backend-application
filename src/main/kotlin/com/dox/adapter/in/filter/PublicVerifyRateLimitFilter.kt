package com.dox.adapter.`in`.filter

import com.dox.extensions.extractClientIp
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.ConcurrentHashMap

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
class PublicVerifyRateLimitFilter : OncePerRequestFilter() {
    private companion object {
        private const val PATH_PREFIX = "/public/verify"
        private const val WINDOW_MS = 60_000L
        private const val MAX_REQUESTS_PER_WINDOW = 60
        private const val MAX_TRACKED_IPS = 5_000
    }

    private val timestampsByIp = ConcurrentHashMap<String, ArrayDeque<Long>>()

    override fun shouldNotFilter(request: HttpServletRequest): Boolean = !request.servletPath.startsWith(PATH_PREFIX)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val ip = request.extractClientIp() ?: "unknown"
        if (!allow(ip)) {
            FilterProblemDetailWriter.write(
                response = response,
                status = 429,
                type = "rate-limit",
                title = "Muitas requisições",
                detail = "Limite de validações excedido. Tente novamente em instantes.",
            )
            return
        }
        filterChain.doFilter(request, response)
    }

    private fun allow(ip: String): Boolean {
        val now = System.currentTimeMillis()
        val cutoff = now - WINDOW_MS

        if (timestampsByIp.size > MAX_TRACKED_IPS) {
            val keysToDrop = timestampsByIp.keys.take(timestampsByIp.size - MAX_TRACKED_IPS)
            keysToDrop.forEach { timestampsByIp.remove(it) }
        }

        val deque = timestampsByIp.computeIfAbsent(ip) { ArrayDeque() }
        synchronized(deque) {
            while (deque.isNotEmpty() && deque.first() < cutoff) deque.removeFirst()
            if (deque.size >= MAX_REQUESTS_PER_WINDOW) return false
            deque.addLast(now)
        }
        return true
    }
}
