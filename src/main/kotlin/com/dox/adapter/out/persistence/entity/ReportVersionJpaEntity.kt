package com.dox.adapter.out.persistence.entity

import com.dox.domain.enum.ReportStatus
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "report_versions")
@EntityListeners(AuditingEntityListener::class)
class ReportVersionJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "report_id", nullable = false)
    var reportId: UUID = UUID.randomUUID(),
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ReportStatus = ReportStatus.RASCUNHO,
    @Column(name = "description")
    var description: String? = null,
    @Column(name = "customer_name")
    var customerName: String? = null,
    @Type(JsonType::class)
    @Column(name = "blocks", columnDefinition = "jsonb")
    var blocks: List<Map<String, Any?>> = emptyList(),
    @Column(name = "type", nullable = false)
    var type: String = "manual",
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null
)
