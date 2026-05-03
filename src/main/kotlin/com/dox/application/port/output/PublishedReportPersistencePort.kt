package com.dox.application.port.output

import com.dox.domain.report.PublishedReport
import java.time.LocalDateTime
import java.util.UUID

interface PublishedReportPersistencePort {
    fun publish(
        reportId: UUID,
        tenantId: UUID,
        contentHash: String,
        finalizedAt: LocalDateTime,
        professionalName: String?,
        professionalCouncil: String?,
        customerName: String?,
    )

    fun findByVerificationCode(code: String): PublishedReport?
}
