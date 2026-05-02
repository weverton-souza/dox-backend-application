package com.dox.adapter.out.persistence.entity

import com.dox.domain.enum.CustomerContactRelationType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import java.util.UUID

@Entity
@Table(name = "customer_contacts")
@SQLRestriction("deleted = false")
class CustomerContactJpaEntity(
    id: UUID = UUID.randomUUID(),
    @Column(name = "customer_id", nullable = false)
    var customerId: UUID = UUID.randomUUID(),
    @Column(name = "name", nullable = false)
    var name: String = "",
    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type", nullable = false)
    var relationType: CustomerContactRelationType = CustomerContactRelationType.OTHER,
    @Column(name = "email")
    var email: String? = null,
    @Column(name = "phone")
    var phone: String? = null,
    @Column(name = "notes", columnDefinition = "TEXT")
    var notes: String? = null,
    @Column(name = "can_receive_forms", nullable = false)
    var canReceiveForms: Boolean = true,
) : AbstractJpaEntity(id = id)
