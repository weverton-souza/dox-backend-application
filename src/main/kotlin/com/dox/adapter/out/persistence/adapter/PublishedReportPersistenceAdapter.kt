package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.PublishedReportJpaEntity
import com.dox.adapter.out.persistence.repository.PublishedReportJpaRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class PublishedReportPersistenceAdapter(
    private val repository: PublishedReportJpaRepository,
) {
    fun publish(
        reportId: UUID,
        tenantId: UUID,
        contentHash: String,
        finalizedAt: LocalDateTime,
        professionalName: String?,
        professionalCrp: String?,
        customerName: String?,
    ) {
        val existing = repository.findByTenantIdAndReportId(tenantId, reportId)
        if (existing != null) return

        repository.save(
            PublishedReportJpaEntity(
                reportId = reportId,
                tenantId = tenantId,
                verificationCode = contentHash.take(16).uppercase(),
                contentHash = contentHash,
                finalizedAt = finalizedAt,
                professionalName = professionalName,
                professionalCrp = professionalCrp,
                customerInitials = customerName?.let(::extractInitials),
            ),
        )
    }

    fun findByVerificationCode(code: String): PublishedReportJpaEntity? {
        val sanitized = code.replace("-", "").trim().uppercase()
        if (sanitized.length != 16 || !sanitized.all { it.isDigit() || it in 'A'..'F' }) return null
        return repository.findByVerificationCode(sanitized)
    }

    private fun extractInitials(name: String): String {
        val skip = setOf("da", "de", "do", "das", "dos", "e", "di", "du")
        return name.split(" ")
            .asSequence()
            .filter { it.isNotBlank() && it.lowercase() !in skip }
            .map { it.first().uppercaseChar() }
            .joinToString(".") { it.toString() }
            .let { if (it.isNotEmpty()) "$it." else "" }
            .take(20)
    }
}
