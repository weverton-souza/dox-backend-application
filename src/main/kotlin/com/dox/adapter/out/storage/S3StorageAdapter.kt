package com.dox.adapter.out.storage

import com.dox.application.port.output.StoragePort
import com.dox.application.port.output.StorageUploadResult
import com.dox.config.StorageProperties
import com.dox.domain.exception.StorageException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.exception.SdkException
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.io.InputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Duration

@Component
class S3StorageAdapter(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
    private val props: StorageProperties,
) : StoragePort {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun upload(
        key: String,
        contentType: String?,
        sizeBytes: Long,
        inputStream: InputStream,
    ): StorageUploadResult {
        try {
            val request =
                PutObjectRequest.builder()
                    .bucket(props.bucket)
                    .key(key)
                    .apply { contentType?.let { contentType(it) } }
                    .contentLength(sizeBytes)
                    .build()

            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, sizeBytes))
            return StorageUploadResult(key = key, sizeBytes = sizeBytes)
        } catch (ex: SdkException) {
            log.error("Falha no upload S3 (key={})", key, ex)
            throw StorageException.UploadFailed(ex.message ?: "erro desconhecido")
        }
    }

    override fun generatePresignedDownloadUrl(
        key: String,
        fileName: String,
        ttl: Duration,
    ): String {
        try {
            val encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20")
            val getRequest =
                GetObjectRequest.builder()
                    .bucket(props.bucket)
                    .key(key)
                    .responseContentDisposition("attachment; filename*=UTF-8''$encodedName")
                    .build()

            val presignRequest =
                GetObjectPresignRequest.builder()
                    .signatureDuration(ttl)
                    .getObjectRequest(getRequest)
                    .build()

            return s3Presigner.presignGetObject(presignRequest).url().toString()
        } catch (ex: NoSuchKeyException) {
            throw StorageException.StorageObjectNotFound(key)
        } catch (ex: SdkException) {
            log.error("Falha ao gerar URL assinada (key={})", key, ex)
            throw StorageException.UploadFailed(ex.message ?: "erro ao gerar URL")
        }
    }

    override fun delete(key: String) {
        try {
            s3Client.deleteObject(
                DeleteObjectRequest.builder()
                    .bucket(props.bucket)
                    .key(key)
                    .build(),
            )
        } catch (ex: SdkException) {
            log.warn("Falha ao remover objeto do storage (key={}): {}", key, ex.message)
        }
    }
}
