package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.DownloadAuditLogJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DownloadAuditLogJpaRepository : JpaRepository<DownloadAuditLogJpaEntity, UUID>
