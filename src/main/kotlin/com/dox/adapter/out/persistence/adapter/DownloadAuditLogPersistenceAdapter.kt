package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.DownloadAuditLogJpaEntity
import com.dox.adapter.out.persistence.repository.DownloadAuditLogJpaRepository
import com.dox.domain.enum.ReportStatus
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class DownloadAuditLogPersistenceAdapter(
    private val repository: DownloadAuditLogJpaRepository,
) {
    fun record(
        reportId: UUID,
        userId: UUID,
        statusAtDownload: ReportStatus,
        ipAddress: String?,
        userAgent: String?,
    ) {
        repository.save(
            DownloadAuditLogJpaEntity(
                reportId = reportId,
                userId = userId,
                statusAtDownload = statusAtDownload,
                ipAddress = ipAddress,
                userAgent = userAgent?.take(500),
            ),
        )
    }
}
