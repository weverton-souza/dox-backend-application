package com.dox.adapter.out.persistence.entity

import com.dox.domain.enum.ReportStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "download_audit_log")
@EntityListeners(AuditingEntityListener::class)
class DownloadAuditLogJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "report_id", nullable = false)
    var reportId: UUID,
    @Column(name = "user_id", nullable = false)
    var userId: UUID,
    @Enumerated(EnumType.STRING)
    @Column(name = "status_at_download", nullable = false)
    var statusAtDownload: ReportStatus,
    @Column(name = "ip_address", length = 45)
    var ipAddress: String? = null,
    @Column(name = "user_agent", length = 500)
    var userAgent: String? = null,
    @CreatedDate
    @Column(name = "downloaded_at", updatable = false)
    var downloadedAt: LocalDateTime? = null,
)
