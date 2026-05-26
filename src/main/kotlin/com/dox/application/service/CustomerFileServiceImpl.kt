package com.dox.application.service

import com.dox.application.port.input.CustomerFileUseCase
import com.dox.application.port.input.UploadCustomerFileCommand
import com.dox.application.port.output.CustomerFilePersistencePort
import com.dox.application.port.output.CustomerPersistencePort
import com.dox.application.port.output.StoragePort
import com.dox.config.StorageProperties
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.exception.StorageException
import com.dox.domain.model.CustomerFile
import com.dox.shared.ContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.UUID

@Service
@Transactional(readOnly = true)
class CustomerFileServiceImpl(
    private val persistencePort: CustomerFilePersistencePort,
    private val customerPersistencePort: CustomerPersistencePort,
    private val storagePort: StoragePort,
    private val props: StorageProperties,
) : CustomerFileUseCase {
    @Transactional
    override fun upload(command: UploadCustomerFileCommand): CustomerFile {
        customerPersistencePort.findById(command.customerId)
            ?: throw ResourceNotFoundException("Cliente", command.customerId.toString())

        validateSize(command.sizeBytes)
        validateMimeType(command.contentType)

        val userId = ContextHolder.getUserIdOrThrow()
        val tenantId = ContextHolder.getTenantIdOrThrow()
        val fileId = UUID.randomUUID()
        val sanitized = sanitizeFileName(command.fileName)
        val key = "tenants/$tenantId/customers/${command.customerId}/files/$fileId/$sanitized"

        storagePort.upload(
            key = key,
            contentType = command.contentType,
            sizeBytes = command.sizeBytes,
            inputStream = command.inputStream,
        )

        return persistencePort.save(
            CustomerFile(
                id = fileId,
                customerId = command.customerId,
                fileName = sanitized,
                fileType = command.contentType,
                category = command.category,
                s3Key = key,
                s3Url = null,
                fileSizeBytes = command.sizeBytes,
                uploadedBy = userId,
            ),
        )
    }

    override fun list(
        customerId: UUID,
        category: String?,
    ): List<CustomerFile> {
        customerPersistencePort.findById(customerId)
            ?: throw ResourceNotFoundException("Cliente", customerId.toString())

        val files = persistencePort.findByCustomerId(customerId)
        return if (category.isNullOrBlank()) files else files.filter { it.category == category }
    }

    override fun getById(
        customerId: UUID,
        fileId: UUID,
    ): CustomerFile = findOrThrow(customerId, fileId)

    override fun generateDownloadUrl(
        customerId: UUID,
        fileId: UUID,
    ): String {
        val file = findOrThrow(customerId, fileId)
        val key = file.s3Key ?: throw StorageException.StorageObjectNotFound(fileId.toString())
        return storagePort.generatePresignedDownloadUrl(
            key = key,
            fileName = file.fileName,
            ttl = Duration.ofMinutes(props.presignedUrlTtlMinutes),
        )
    }

    @Transactional
    override fun delete(
        customerId: UUID,
        fileId: UUID,
    ) {
        val file = findOrThrow(customerId, fileId)
        persistencePort.softDelete(file.id)
        file.s3Key?.let { storagePort.delete(it) }
    }

    private fun findOrThrow(
        customerId: UUID,
        fileId: UUID,
    ): CustomerFile {
        val file =
            persistencePort.findById(fileId)
                ?: throw ResourceNotFoundException("Arquivo", fileId.toString())
        if (file.customerId != customerId) {
            throw ResourceNotFoundException("Arquivo", fileId.toString())
        }
        return file
    }

    private fun validateSize(sizeBytes: Long) {
        val maxBytes = props.maxFileSizeMb * 1024 * 1024
        if (sizeBytes <= 0 || sizeBytes > maxBytes) {
            throw StorageException.FileSizeExceeded(props.maxFileSizeMb, sizeBytes)
        }
    }

    private fun validateMimeType(contentType: String?) {
        val allowed = props.allowedMimeTypesList()
        if (contentType.isNullOrBlank() || contentType !in allowed) {
            throw StorageException.MimeTypeNotAllowed(contentType, allowed)
        }
    }

    private fun sanitizeFileName(name: String): String {
        val trimmed = name.trim().ifBlank { "arquivo" }
        return trimmed.replace(Regex("[\\\\/:*?\"<>|\\s]+"), "_").take(255)
    }
}
