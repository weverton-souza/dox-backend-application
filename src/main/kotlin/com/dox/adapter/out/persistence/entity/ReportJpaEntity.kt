package com.dox.adapter.out.persistence.entity

import com.dox.domain.enum.ReportStatus
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import java.util.UUID

@Entity
@Table(name = "reports")
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
) : AbstractJpaEntity()
