package com.dox.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "asaas_customers", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class AsaasCustomerJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false, unique = true)
    var tenantId: UUID,
    @Column(name = "asaas_customer_id", nullable = false, unique = true, length = 255)
    var asaasCustomerId: String,
    @Column(name = "cpf_cnpj", nullable = false, length = 20)
    var cpfCnpj: String,
    @Column(name = "email", length = 255)
    var email: String? = null,
    @Column(name = "name", nullable = false, length = 255)
    var name: String,
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
