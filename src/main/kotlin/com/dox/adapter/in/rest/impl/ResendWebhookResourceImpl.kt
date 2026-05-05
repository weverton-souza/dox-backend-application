package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.resource.ResendWebhookResource
import com.dox.adapter.out.email.webhook.ResendSignatureVerifier
import com.dox.application.port.input.EmailUseCase
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ResendWebhookResourceImpl(
    private val signatureVerifier: ResendSignatureVerifier,
    private val emailUseCase: EmailUseCase,
    private val objectMapper: ObjectMapper,
) : ResendWebhookResource {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun receive(
        svixId: String?,
        svixTimestamp: String?,
        svixSignature: String?,
        rawBody: String,
    ): ResponseEntity<Void> {
        val verified = signatureVerifier.verify(svixId, svixTimestamp, svixSignature, rawBody)
        if (!verified) {
            log.warn("Resend webhook signature verification failed (svix-id={})", svixId)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        return try {
            val node = objectMapper.readTree(rawBody)
            val type = node.path("type").asText("")
            val providerId = node.path("data").path("email_id").asText("").ifBlank { null }
            val errorMessage =
                node.path("data").path("bounce").path("message").asText("").ifBlank {
                    node.path("data").path("error").asText("").ifBlank { null }
                }

            if (providerId != null) {
                emailUseCase.handleProviderEvent(providerId, type, errorMessage)
            } else {
                log.warn("Resend webhook missing email_id (type={})", type)
            }
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            log.error("Failed processing Resend webhook: {}", e.message, e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }
}
