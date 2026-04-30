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
@Table(name = "payment_methods_card", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class PaymentMethodCardJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID,
    @Column(name = "asaas_credit_card_token", nullable = false, length = 255)
    var asaasCreditCardToken: String,
    @Column(name = "brand", nullable = false, length = 30)
    var brand: String,
    @Column(name = "last4", nullable = false, length = 4)
    var last4: String,
    @Column(name = "holder_name", nullable = false, length = 255)
    var holderName: String,
    @Column(name = "is_default", nullable = false)
    var isDefault: Boolean = false,
    @Column(name = "expires_at")
    var expiresAt: LocalDate? = null,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
