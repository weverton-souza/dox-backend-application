package com.dox.application.port.input

import com.dox.domain.model.CustomerFile
import java.io.InputStream
import java.util.UUID

data class UploadCustomerFileCommand(
    val customerId: UUID,
    val fileName: String,
    val contentType: String?,
    val sizeBytes: Long,
    val category: String?,
    val inputStream: InputStream,
)

interface CustomerFileUseCase {
    fun upload(command: UploadCustomerFileCommand): CustomerFile

    fun list(
        customerId: UUID,
        category: String? = null,
    ): List<CustomerFile>

    fun getById(
        customerId: UUID,
        fileId: UUID,
    ): CustomerFile

    fun generateDownloadUrl(
        customerId: UUID,
        fileId: UUID,
    ): String

    fun delete(
        customerId: UUID,
        fileId: UUID,
    )
}
