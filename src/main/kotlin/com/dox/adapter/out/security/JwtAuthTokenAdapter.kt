package com.dox.adapter.out.security

import com.dox.application.port.output.AuthTokenPort
import com.dox.application.port.output.FormLinkTokenData
import com.dox.config.SecurityProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Component
class JwtAuthTokenAdapter(
    private val securityProperties: SecurityProperties
) : AuthTokenPort {

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(securityProperties.jwtSigningKey.toByteArray())
    }

    override fun generateAccessToken(userId: UUID, email: String, tenantId: UUID): String {
        val now = Date()
        val expiry = Date(now.time + securityProperties.accessTokenExpiration)

        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("tenantId", tenantId.toString())
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    override fun generateRefreshToken(): String = UUID.randomUUID().toString()

    override fun validateAccessToken(token: String): Boolean =
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            true
        } catch (_: Exception) {
            false
        }

    override fun extractUserId(token: String): UUID {
        val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        return UUID.fromString(claims.subject)
    }

    override fun extractEmail(token: String): String {
        val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        return claims["email"] as String
    }

    override fun extractTenantId(token: String): UUID {
        val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        return UUID.fromString(claims["tenantId"] as String)
    }

    override fun generateFormLinkToken(tenantId: UUID, formLinkId: UUID, expiresAt: LocalDateTime): String {
        val expiry = Date.from(expiresAt.atZone(ZoneId.systemDefault()).toInstant())

        return Jwts.builder()
            .claim("type", "form_link")
            .claim("tenantId", tenantId.toString())
            .claim("formLinkId", formLinkId.toString())
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    override fun extractFormLinkData(token: String): FormLinkTokenData {
        val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        require(claims["type"] == "form_link") { "Token type invalid" }
        return FormLinkTokenData(
            tenantId = UUID.fromString(claims["tenantId"] as String),
            formLinkId = UUID.fromString(claims["formLinkId"] as String)
        )
    }
}
