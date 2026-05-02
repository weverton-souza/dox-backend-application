package com.dox.adapter.out.persistence.entity

import com.dox.domain.enum.FormResponseStatus
import com.dox.domain.enum.RespondentType
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
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "form_responses")
@EntityListeners(AuditingEntityListener::class)
class FormResponseJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "form_id", nullable = false)
    var formId: UUID = UUID.randomUUID(),
    @Column(name = "form_version_id", nullable = false)
    var formVersionId: UUID = UUID.randomUUID(),
    @Column(name = "customer_id")
    var customerId: UUID? = null,
    @Column(name = "customer_name")
    var customerName: String? = null,
    @Column(name = "customer_contact_id")
    var customerContactId: UUID? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "respondent_type", nullable = false, length = 50)
    var respondentType: RespondentType = RespondentType.CUSTOMER,
    @Column(name = "respondent_name")
    var respondentName: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: FormResponseStatus = FormResponseStatus.EM_ANDAMENTO,
    @Type(JsonType::class)
    @Column(name = "answers", columnDefinition = "jsonb")
    var answers: List<Map<String, Any?>> = emptyList(),
    @Column(name = "generated_report_id")
    var generatedReportId: UUID? = null,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
