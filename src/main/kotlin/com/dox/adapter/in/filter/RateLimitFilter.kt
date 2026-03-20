package com.dox.adapter.`in`.filter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.atomic.AtomicLong

@Component
class RateLimitFilter(
    private val objectMapper: ObjectMapper,
    @param:Value("\${RATE_LIMIT_LOGIN_MAX:5}")
    private val loginMaxAttempts: Int,
    @param:Value("\${RATE_LIMIT_LOGIN_WINDOW:60}")
    private val loginWindowSeconds: Long,
    @param:Value("\${RATE_LIMIT_REGISTER_MAX:3}")
    private val registerMaxAttempts: Int,
    @param:Value("\${RATE_LIMIT_REGISTER_WINDOW:60}")
    private val registerWindowSeconds: Long
) : OncePerRequestFilter() {

    private val attempts = ConcurrentHashMap<String, ConcurrentLinkedDeque<Instant>>()
    private val lastCleanup = AtomicLong(Instant.now().epochSecond)

    private val rateLimitedPaths = mapOf(
        "/auth/login" to { Pair(loginMaxAttempts, loginWindowSeconds) },
        "/auth/register" to { Pair(registerMaxAttempts, registerWindowSeconds) }
    )

    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        request.method != "POST" || rateLimitedPaths.keys.none { request.servletPath == it }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
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
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.characterEncoding = "UTF-8"
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.setHeader("Retry-After", retryAfter.coerceAtLeast(1).toString())
            response.writer.write(
                objectMapper.writeValueAsString(
                    mapOf(
                        "type" to "urn:dox:error:RATE_LIMITED",
                        "title" to "Muitas tentativas",
                        "status" to 429,
                        "detail" to "Limite de requisições excedido. Tente novamente em ${retryAfter}s"
                    )
                )
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
        val forwarded = request.getHeader("X-Forwarded-For")
        return forwarded?.split(",")?.first()?.trim() ?: request.remoteAddr
    }
}
