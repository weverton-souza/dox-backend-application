package com.dox.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "users", schema = "public")
class UserJpaEntity(
    @Column(name = "email", unique = true, nullable = false)
    var email: String = "",

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_tenant_id")
    var personalTenant: TenantJpaEntity? = null
) : AbstractJpaEntity()
