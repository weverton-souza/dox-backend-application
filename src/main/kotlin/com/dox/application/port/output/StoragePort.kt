package com.dox.application.port.output

import java.io.InputStream
import java.time.Duration

data class StorageUploadResult(
    val key: String,
    val sizeBytes: Long,
)

interface StoragePort {
    fun upload(
        key: String,
        contentType: String?,
        sizeBytes: Long,
        inputStream: InputStream,
    ): StorageUploadResult

    fun generatePresignedDownloadUrl(
        key: String,
        fileName: String,
        ttl: Duration,
    ): String

    fun delete(key: String)
}
