package com.dox.adapter.`in`.rest.dto.file

import com.dox.domain.model.CustomerFile
import java.time.LocalDateTime
import java.util.UUID

data class CustomerFileResponse(
    val id: UUID,
    val customerId: UUID,
    val fileName: String,
    val fileType: String?,
    val category: String?,
    val fileSizeBytes: Long?,
    val uploadedBy: UUID?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
) {
    companion object {
        fun from(file: CustomerFile) =
            CustomerFileResponse(
                id = file.id,
                customerId = file.customerId,
                fileName = file.fileName,
                fileType = file.fileType,
                category = file.category,
                fileSizeBytes = file.fileSizeBytes,
                uploadedBy = file.uploadedBy,
                createdAt = file.createdAt,
                updatedAt = file.updatedAt,
            )
    }
}

data class CustomerFileDownloadUrlResponse(
    val url: String,
    val expiresInMinutes: Long,
)
