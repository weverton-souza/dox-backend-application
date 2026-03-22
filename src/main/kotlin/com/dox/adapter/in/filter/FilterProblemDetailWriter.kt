package com.dox.adapter.`in`.filter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.servlet.http.HttpServletResponse
import java.time.Instant

object FilterProblemDetailWriter {

    private val objectMapper = jacksonObjectMapper()

    fun write(response: HttpServletResponse, status: Int, type: String, title: String, detail: String) {
        response.status = status
        response.contentType = "application/problem+json"
        response.characterEncoding = "UTF-8"
        val body = mapOf(
            "type" to "urn:dox:error:$type",
            "title" to title,
            "status" to status,
            "detail" to detail,
            "instance" to "",
            "properties" to mapOf(
                "errorCode" to type,
                "timestamp" to Instant.now().toString()
            )
        )
        response.writer.write(objectMapper.writeValueAsString(body))
    }
}
