package com.dox.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "customer_events")
class CustomerEventJpaEntity(
    id: UUID = UUID.randomUUID(),
    @Column(name = "customer_id", nullable = false)
    var customerId: UUID = UUID.randomUUID(),
    @Column(name = "type", nullable = false)
    var type: String = "",
    @Column(name = "title", nullable = false)
    var title: String = "",
    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,
    @Column(name = "date", nullable = false)
    var date: LocalDateTime = LocalDateTime.now()
) : AbstractJpaEntity(id = id)
