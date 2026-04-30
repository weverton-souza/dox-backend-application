package com.dox.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "subscriptions", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class SubscriptionJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false, unique = true)
    var tenantId: UUID,
    @Column(name = "asaas_subscription_id", unique = true, length = 255)
    var asaasSubscriptionId: String? = null,
    @Column(name = "status", nullable = false, length = 20)
    var status: String,
    @Column(name = "billing_cycle", nullable = false, length = 20)
    var billingCycle: String,
    @Column(name = "billing_type", nullable = false, length = 20)
    var billingType: String,
    @Column(name = "value_cents", nullable = false)
    var valueCents: Int,
    @Column(name = "current_period_start")
    var currentPeriodStart: LocalDateTime? = null,
    @Column(name = "current_period_end")
    var currentPeriodEnd: LocalDateTime? = null,
    @Column(name = "next_due_date")
    var nextDueDate: LocalDate? = null,
    @Column(name = "trial_end")
    var trialEnd: LocalDateTime? = null,
    @Column(name = "canceled_at")
    var canceledAt: LocalDateTime? = null,
    @Column(name = "cancel_effective_at")
    var cancelEffectiveAt: LocalDateTime? = null,
    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    var cancelReason: String? = null,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
