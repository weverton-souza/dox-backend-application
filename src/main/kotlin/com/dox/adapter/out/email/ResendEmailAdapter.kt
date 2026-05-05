package com.dox.adapter.out.email

import com.dox.adapter.out.email.config.EmailProperties
import com.dox.application.port.output.EmailPort
import com.dox.domain.email.EmailMessage
import com.dox.domain.email.EmailSendResult
import com.resend.Resend
import com.resend.core.exception.ResendException
import com.resend.services.emails.model.CreateEmailOptions
import com.resend.services.emails.model.Tag
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component("resendEmailAdapter")
class ResendEmailAdapter(
    private val resend: Resend,
    private val properties: EmailProperties,
) : EmailPort {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun send(message: EmailMessage): EmailSendResult {
        if (!isEnabled()) {
            log.info("Email disabled, skipping send to {}", message.to)
            return EmailSendResult(
                providerId = null,
                sentAt = LocalDateTime.now(),
                accepted = false,
                errorMessage = "email-disabled",
            )
        }

        val builder =
            CreateEmailOptions.builder()
                .from(message.from ?: properties.fromAddress)
                .to(message.to)
                .subject(message.subject)
                .html(message.html)

        message.text?.let { builder.text(it) }

        val replyTo = message.replyTo ?: properties.replyTo.takeIf { it.isNotBlank() }
        replyTo?.let { builder.replyTo(it) }

        if (message.cc.isNotEmpty()) builder.cc(message.cc)
        if (message.bcc.isNotEmpty()) builder.bcc(message.bcc)

        if (message.tags.isNotEmpty()) {
            val tags =
                message.tags.entries.map {
                    Tag.builder()
                        .name(sanitizeTagToken(it.key))
                        .value(sanitizeTagToken(it.value))
                        .build()
                }
            builder.tags(tags)
        }

        return try {
            val response = resend.emails().send(builder.build())
            EmailSendResult(
                providerId = response.id,
                sentAt = LocalDateTime.now(),
                accepted = true,
            )
        } catch (e: ResendException) {
            log.error("Resend send failed: {}", e.message)
            EmailSendResult(
                providerId = null,
                sentAt = LocalDateTime.now(),
                accepted = false,
                errorMessage = e.message,
            )
        }
    }

    override fun isEnabled(): Boolean = properties.enabled && properties.apiKey.isNotBlank()

    private fun sanitizeTagToken(value: String): String = value.take(MAX_TAG_LEN).replace(Regex("[^A-Za-z0-9_-]"), "_")

    companion object {
        private const val MAX_TAG_LEN = 256
    }
}
