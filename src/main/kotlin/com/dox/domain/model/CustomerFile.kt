package com.dox.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class CustomerFile(
    val id: UUID = UUID.randomUUID(),
    val customerId: UUID,
    val fileName: String,
    val fileType: String? = null,
    val category: String? = null,
    val s3Key: String? = null,
    val s3Url: String? = null,
    val fileSizeBytes: Long? = null,
    val uploadedBy: UUID? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
