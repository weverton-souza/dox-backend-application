package com.dox.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "customer_files")
@EntityListeners(AuditingEntityListener::class)
@SQLRestriction("deleted = false")
class CustomerFileJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "customer_id", nullable = false)
    var customerId: UUID = UUID.randomUUID(),

    @Column(name = "file_name", nullable = false)
    var fileName: String = "",

    @Column(name = "file_type")
    var fileType: String? = null,

    @Column(name = "category")
    var category: String? = null,

    @Column(name = "s3_key")
    var s3Key: String? = null,

    @Column(name = "s3_url")
    var s3Url: String? = null,

    @Column(name = "file_size_bytes")
    var fileSizeBytes: Long? = null,

    @Column(name = "uploaded_by")
    var uploadedBy: UUID? = null,

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Column(name = "deleted", nullable = false)
    var deleted: Boolean = false
)
