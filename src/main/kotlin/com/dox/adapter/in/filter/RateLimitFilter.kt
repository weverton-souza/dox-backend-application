package com.dox.adapter.`in`.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.atomic.AtomicLong

@Component
class RateLimitFilter(
    @param:Value("\${RATE_LIMIT_LOGIN_MAX:5}")
    private val loginMaxAttempts: Int,
    @param:Value("\${RATE_LIMIT_LOGIN_WINDOW:60}")
    private val loginWindowSeconds: Long,
    @param:Value("\${RATE_LIMIT_REGISTER_MAX:3}")
    private val registerMaxAttempts: Int,
    @param:Value("\${RATE_LIMIT_REGISTER_WINDOW:60}")
    private val registerWindowSeconds: Long,
    @param:Value("\${TRUSTED_PROXIES:}")
    trustedProxiesConfig: String,
) : OncePerRequestFilter() {
    private val trustedProxies: Set<String> =
        trustedProxiesConfig
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()

    private val attempts = ConcurrentHashMap<String, ConcurrentLinkedDeque<Instant>>()
    private val lastCleanup = AtomicLong(Instant.now().epochSecond)

    private val rateLimitedPaths =
        mapOf(
            "/auth/login" to { Pair(loginMaxAttempts, loginWindowSeconds) },
            "/auth/register" to { Pair(registerMaxAttempts, registerWindowSeconds) },
            "/admin/auth/login" to { Pair(loginMaxAttempts, loginWindowSeconds) },
        )

    override fun shouldNotFilter(request: HttpServletRequest): Boolean = request.method != "POST" || rateLimitedPaths.keys.none { request.servletPath == it }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val clientIp = resolveClientIp(request)
        val path = request.servletPath
        val (maxAttempts, windowSeconds) = rateLimitedPaths[path]!!.invoke()

        cleanupStaleEntries(windowSeconds)

        val key = "$clientIp:$path"
        val now = Instant.now()
        val windowStart = now.minusSeconds(windowSeconds)

        val timestamps = attempts.computeIfAbsent(key) { ConcurrentLinkedDeque() }
        timestamps.removeIf { it.isBefore(windowStart) }

        if (timestamps.size >= maxAttempts) {
            val retryAfter = timestamps.first.plusSeconds(windowSeconds).epochSecond - now.epochSecond
            response.setHeader("Retry-After", retryAfter.coerceAtLeast(1).toString())
            FilterProblemDetailWriter.write(
                response,
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "RATE_LIMITED",
                "Muitas tentativas",
                "Limite de requisições excedido. Tente novamente em ${retryAfter}s",
            )
            return
        }

        timestamps.add(now)
        filterChain.doFilter(request, response)
    }

    private fun cleanupStaleEntries(windowSeconds: Long) {
        val now = Instant.now().epochSecond
        val last = lastCleanup.get()
        if (now - last < windowSeconds * 2) return
        if (!lastCleanup.compareAndSet(last, now)) return

        val cutoff = Instant.now().minusSeconds(windowSeconds * 2)
        attempts.entries.removeIf { (_, deque) ->
            deque.removeIf { it.isBefore(cutoff) }
            deque.isEmpty()
        }
    }

    private fun resolveClientIp(request: HttpServletRequest): String {
        if (trustedProxies.isNotEmpty()) {
            val forwarded = request.getHeader("X-Forwarded-For")
            if (forwarded != null && request.remoteAddr in trustedProxies) {
                return forwarded.split(",").first().trim()
            }
        }
        return request.remoteAddr
    }
}
