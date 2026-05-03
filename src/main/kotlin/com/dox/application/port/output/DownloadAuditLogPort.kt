package com.dox.application.port.output

import com.dox.domain.enum.ReportStatus
import java.util.UUID

interface DownloadAuditLogPort {
    fun record(
        reportId: UUID,
        userId: UUID,
        statusAtDownload: ReportStatus,
        ipAddress: String?,
        userAgent: String?,
    )
}
