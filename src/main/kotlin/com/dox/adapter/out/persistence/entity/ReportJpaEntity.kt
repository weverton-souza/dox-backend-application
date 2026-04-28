package com.dox.adapter.out.persistence.entity

import com.dox.domain.enum.ReportStatus
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.SQLRestriction
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "reports")
@SQLRestriction("deleted = false")
class ReportJpaEntity(
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ReportStatus = ReportStatus.RASCUNHO,
    @Column(name = "customer_name")
    var customerName: String? = null,
    @Column(name = "customer_id")
    var customerId: UUID? = null,
    @Column(name = "form_response_id")
    var formResponseId: UUID? = null,
    @Column(name = "template_id")
    var templateId: UUID? = null,
    @Column(name = "is_structure_locked")
    var isStructureLocked: Boolean = false,
    @Type(JsonType::class)
    @Column(name = "blocks", columnDefinition = "jsonb")
    var blocks: List<Map<String, Any?>> = emptyList(),
    @Column(name = "finalized_at")
    var finalizedAt: LocalDateTime? = null,
    @Column(name = "content_hash", length = 64)
    var contentHash: String? = null,
    @Version
    @Column(name = "version", nullable = false)
    var version: Long = 0,
) : AbstractJpaEntity()
