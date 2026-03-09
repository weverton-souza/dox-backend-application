package com.dox.adapter.out.persistence.entity

import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.Type

@Entity
@Table(name = "customers")
class CustomerJpaEntity(
    @Type(JsonType::class)
    @Column(name = "data", columnDefinition = "jsonb")
    var data: Map<String, Any?> = emptyMap()
) : AbstractJpaEntity()
