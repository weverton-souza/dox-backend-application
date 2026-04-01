package com.dox.adapter.`in`.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RequestSizeLimitFilter(
    @param:Value("\${MAX_REQUEST_BODY_BYTES:2097152}")
    private val maxBodyBytes: Long,
) : OncePerRequestFilter() {
    override fun shouldNotFilter(request: HttpServletRequest): Boolean = request.method in setOf("GET", "DELETE", "OPTIONS", "HEAD")

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val contentLength = request.contentLengthLong
        if (contentLength > maxBodyBytes || (contentLength == -1L && request.getHeader("Transfer-Encoding") != null)) {
            FilterProblemDetailWriter.write(
                response,
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                "PAYLOAD_TOO_LARGE",
                "Payload muito grande",
                "O corpo da requisição excede o limite de ${maxBodyBytes / 1024}KB",
            )
            return
        }
        filterChain.doFilter(request, response)
    }
}
