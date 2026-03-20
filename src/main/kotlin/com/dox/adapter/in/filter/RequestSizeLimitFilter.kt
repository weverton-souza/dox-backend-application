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

@Component
class RequestSizeLimitFilter(
    private val objectMapper: ObjectMapper,
    @param:Value("\${MAX_REQUEST_BODY_BYTES:2097152}")
    private val maxBodyBytes: Long
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        request.method in setOf("GET", "DELETE", "OPTIONS", "HEAD")

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val contentLength = request.contentLengthLong
        if (contentLength > maxBodyBytes || (contentLength == -1L && request.getHeader("Transfer-Encoding") != null)) {
            rejectPayload(response)
            return
        }
        filterChain.doFilter(request, response)
    }

    private fun rejectPayload(response: HttpServletResponse) {
        response.status = HttpStatus.PAYLOAD_TOO_LARGE.value()
        response.characterEncoding = "UTF-8"
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(
            objectMapper.writeValueAsString(
                mapOf(
                    "type" to "urn:dox:error:PAYLOAD_TOO_LARGE",
                    "title" to "Payload muito grande",
                    "status" to 413,
                    "detail" to "O corpo da requisição excede o limite de ${maxBodyBytes / 1024}KB"
                )
            )
        )
    }
}
