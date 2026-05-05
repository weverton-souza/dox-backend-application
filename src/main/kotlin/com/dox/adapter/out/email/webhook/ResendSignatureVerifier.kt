package com.dox.adapter.out.email.webhook

import com.dox.adapter.out.email.config.EmailProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class ResendSignatureVerifier(
    private val properties: EmailProperties,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun verify(
        svixId: String?,
        svixTimestamp: String?,
        svixSignature: String?,
        rawBody: String,
    ): Boolean {
        if (properties.webhookSecret.isBlank()) {
            log.warn("Webhook secret not configured — webhook signature verification SKIPPED")
            return true
        }

        if (svixId.isNullOrBlank() || svixTimestamp.isNullOrBlank() || svixSignature.isNullOrBlank()) {
            return false
        }

        val timestampInstant =
            runCatching { Instant.ofEpochSecond(svixTimestamp.toLong()) }
                .getOrElse { return false }

        if (Duration.between(timestampInstant, Instant.now()).abs() > MAX_TIMESTAMP_SKEW) {
            return false
        }

        val secret = stripWhsecPrefix(properties.webhookSecret)
        val secretBytes = Base64.getDecoder().decode(secret)
        val payload = "$svixId.$svixTimestamp.$rawBody"

        val expected = base64HmacSha256(secretBytes, payload)

        return svixSignature.split(" ").any { sig ->
            val parts = sig.split(",", limit = 2)
            parts.size == 2 && parts[0] == "v1" && constantTimeEquals(parts[1], expected)
        }
    }

    private fun stripWhsecPrefix(value: String): String = value.removePrefix("whsec_")

    private fun base64HmacSha256(
        secret: ByteArray,
        payload: String,
    ): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secret, "HmacSHA256"))
        val raw = mac.doFinal(payload.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(raw)
    }

    private fun constantTimeEquals(
        a: String,
        b: String,
    ): Boolean {
        if (a.length != b.length) return false
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].code xor b[i].code)
        }
        return result == 0
    }

    companion object {
        private val MAX_TIMESTAMP_SKEW = Duration.ofMinutes(5)
    }
}
