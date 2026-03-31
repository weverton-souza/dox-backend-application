package com.dox.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "customer_notes")
class CustomerNoteJpaEntity(
    id: UUID = UUID.randomUUID(),
    @Column(name = "customer_id", nullable = false)
    var customerId: UUID = UUID.randomUUID(),
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    var content: String = ""
) : AbstractJpaEntity(id = id)
