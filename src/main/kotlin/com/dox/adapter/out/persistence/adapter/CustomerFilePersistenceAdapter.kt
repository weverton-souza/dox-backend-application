package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.CustomerFileJpaEntity
import com.dox.adapter.out.persistence.repository.CustomerFileJpaRepository
import com.dox.application.port.output.CustomerFilePersistencePort
import com.dox.domain.model.CustomerFile
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CustomerFilePersistenceAdapter(
    private val repository: CustomerFileJpaRepository
) : CustomerFilePersistencePort {
    override fun save(customerFile: CustomerFile): CustomerFile =
        repository.save(customerFile.toEntity()).toDomain()

    override fun findByCustomerId(customerId: UUID): List<CustomerFile> =
        repository.findByCustomerIdAndDeletedFalse(customerId).map { it.toDomain() }

    override fun findById(id: UUID): CustomerFile? =
        repository.findById(id).orElse(null)?.toDomain()

    private fun CustomerFile.toEntity() = CustomerFileJpaEntity().apply {
        id = this@toEntity.id
        customerId = this@toEntity.customerId
        fileName = this@toEntity.fileName
        fileType = this@toEntity.fileType
        category = this@toEntity.category
        s3Key = this@toEntity.s3Key
        s3Url = this@toEntity.s3Url
        fileSizeBytes = this@toEntity.fileSizeBytes
        uploadedBy = this@toEntity.uploadedBy
    }

    private fun CustomerFileJpaEntity.toDomain() = CustomerFile(
        id = id,
        customerId = customerId,
        fileName = fileName,
        fileType = fileType,
        category = category,
        s3Key = s3Key,
        s3Url = s3Url,
        fileSizeBytes = fileSizeBytes,
        uploadedBy = uploadedBy,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
