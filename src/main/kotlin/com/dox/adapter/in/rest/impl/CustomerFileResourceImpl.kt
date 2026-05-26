package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.file.CustomerFileDownloadUrlResponse
import com.dox.adapter.`in`.rest.dto.file.CustomerFileResponse
import com.dox.adapter.`in`.rest.resource.CustomerFileResource
import com.dox.application.port.input.CustomerFileUseCase
import com.dox.application.port.input.UploadCustomerFileCommand
import com.dox.config.StorageProperties
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
class CustomerFileResourceImpl(
    private val customerFileUseCase: CustomerFileUseCase,
    private val storageProperties: StorageProperties,
) : CustomerFileResource {
    override fun list(
        customerId: UUID,
        category: String?,
    ): ResponseEntity<List<CustomerFileResponse>> =
        responseEntity(
            customerFileUseCase.list(customerId, category).map(CustomerFileResponse::from),
        )

    override fun upload(
        customerId: UUID,
        file: MultipartFile,
        category: String?,
    ): ResponseEntity<CustomerFileResponse> {
        val result =
            file.inputStream.use { stream ->
                customerFileUseCase.upload(
                    UploadCustomerFileCommand(
                        customerId = customerId,
                        fileName = file.originalFilename ?: "arquivo",
                        contentType = file.contentType,
                        sizeBytes = file.size,
                        category = category,
                        inputStream = stream,
                    ),
                )
            }
        return responseEntity(CustomerFileResponse.from(result), HttpStatus.CREATED)
    }

    override fun getById(
        customerId: UUID,
        fileId: UUID,
    ): ResponseEntity<CustomerFileResponse> {
        val file = customerFileUseCase.getById(customerId, fileId)
        return responseEntity(CustomerFileResponse.from(file))
    }

    override fun getDownloadUrl(
        customerId: UUID,
        fileId: UUID,
    ): ResponseEntity<CustomerFileDownloadUrlResponse> {
        val url = customerFileUseCase.generateDownloadUrl(customerId, fileId)
        return responseEntity(
            CustomerFileDownloadUrlResponse(
                url = url,
                expiresInMinutes = storageProperties.presignedUrlTtlMinutes,
            ),
        )
    }

    override fun delete(
        customerId: UUID,
        fileId: UUID,
    ): ResponseEntity<Void> {
        customerFileUseCase.delete(customerId, fileId)
        return noContent()
    }
}
