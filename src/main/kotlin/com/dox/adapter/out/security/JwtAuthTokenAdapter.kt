package com.dox.adapter.out.security

import com.dox.application.port.output.AuthTokenPort
import com.dox.application.port.output.FormLinkTokenData
import com.dox.config.SecurityProperties
import com.dox.domain.enum.AdminRole
import com.dox.domain.exception.InvalidTokenException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Component
class JwtAuthTokenAdapter(
    private val securityProperties: SecurityProperties,
) : AuthTokenPort {
    private val log = LoggerFactory.getLogger(javaClass)

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(securityProperties.jwtSigningKey.toByteArray())
    }

    private val parser by lazy {
        Jwts.parser().verifyWith(key).build()
    }

    private fun parseClaims(token: String) = parser.parseSignedClaims(token).payload

    override fun generateAccessToken(
        userId: UUID,
        email: String,
        tenantId: UUID,
    ): String {
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
            val claims = parseClaims(token)
            claims.subject != null && claims["type"] == null
        } catch (e: io.jsonwebtoken.ExpiredJwtException) {
            log.debug("JWT token expired: {}", e.message)
            false
        } catch (e: io.jsonwebtoken.security.SecurityException) {
            log.warn("JWT signature validation failed: {}", e.message)
            false
        } catch (e: io.jsonwebtoken.MalformedJwtException) {
            log.warn("Malformed JWT token: {}", e.message)
            false
        } catch (e: Exception) {
            log.warn("JWT validation failed: {}", e.message)
            false
        }

    override fun extractUserId(token: String): UUID = UUID.fromString(parseClaims(token).subject)

    override fun extractEmail(token: String): String =
        parseClaims(token)["email"] as? String
            ?: throw InvalidTokenException("Token sem email")

    override fun extractTenantId(token: String): UUID {
        val tenantId =
            parseClaims(token)["tenantId"] as? String
                ?: throw InvalidTokenException("Token sem tenantId")
        return UUID.fromString(tenantId)
    }

    override fun generateFormLinkToken(
        tenantId: UUID,
        formLinkId: UUID,
        expiresAt: LocalDateTime,
    ): String {
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
        val claims = parseClaims(token)
        if (claims["type"] != "form_link") throw InvalidTokenException("Tipo de token inválido")
        val tenantId =
            claims["tenantId"] as? String
                ?: throw InvalidTokenException("Token sem tenantId")
        val formLinkId =
            claims["formLinkId"] as? String
                ?: throw InvalidTokenException("Token sem formLinkId")
        return FormLinkTokenData(
            tenantId = UUID.fromString(tenantId),
            formLinkId = UUID.fromString(formLinkId),
        )
    }

    override fun generateAdminAccessToken(
        adminId: UUID,
        email: String,
        role: AdminRole,
    ): String {
        val now = Date()
        val expiry = Date(now.time + securityProperties.adminAccessTokenExpiration)

        return Jwts.builder()
            .subject(adminId.toString())
            .claim("email", email)
            .claim("isAdmin", true)
            .claim("adminRole", role.name)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    override fun isAdminToken(token: String): Boolean =
        try {
            parseClaims(token)["isAdmin"] == true
        } catch (e: Exception) {
            false
        }

    override fun extractAdminRole(token: String): AdminRole {
        val role =
            parseClaims(token)["adminRole"] as? String
                ?: throw InvalidTokenException("Token sem adminRole")
        return AdminRole.valueOf(role)
    }
}
