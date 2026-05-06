package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.EmailLogJpaEntity
import com.dox.adapter.out.persistence.repository.EmailLogJpaRepository
import com.dox.application.port.output.EmailLogPersistencePort
import com.dox.domain.email.EmailLog
import com.dox.domain.email.EmailLogStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Component
class EmailLogPersistenceAdapter(
    private val repository: EmailLogJpaRepository,
) : EmailLogPersistencePort {
    override fun save(log: EmailLog): EmailLog {
        val entity =
            repository.findById(log.id).orElseGet { EmailLogJpaEntity(id = log.id) }.apply {
                tenantId = log.tenantId
                templateId = log.templateId
                recipientEmail = log.recipientEmail
                subject = log.subject
                providerId = log.providerId
                status = log.status
                errorMessage = log.errorMessage
                idempotencyKey = log.idempotencyKey
                tags = log.tags
                formLinkId = log.formLinkId
            }
        return repository.save(entity).toDomain()
    }

    override fun findById(id: UUID): EmailLog? = repository.findById(id).orElse(null)?.toDomain()

    override fun findByProviderId(providerId: String): EmailLog? = repository.findByProviderId(providerId)?.toDomain()

    override fun findByIdempotencyKey(key: String): EmailLog? = repository.findByIdempotencyKey(key)?.toDomain()

    override fun findByFormLinkId(formLinkId: UUID): List<EmailLog> = repository.findByFormLinkIdOrderBySentAtDesc(formLinkId).map { it.toDomain() }

    override fun findPaginated(
        templateId: String?,
        status: EmailLogStatus?,
        recipientEmail: String?,
        tenantId: UUID?,
        pageable: Pageable,
    ): Page<EmailLog> = repository.findFiltered(templateId, status, recipientEmail, tenantId, pageable).map { it.toDomain() }

    @Transactional
    override fun updateStatus(
        providerId: String,
        status: EmailLogStatus,
        errorMessage: String?,
    ): Int = repository.updateStatusByProviderId(providerId, status, errorMessage, LocalDateTime.now())

    private fun EmailLogJpaEntity.toDomain() =
        EmailLog(
            id = id,
            tenantId = tenantId,
            templateId = templateId,
            recipientEmail = recipientEmail,
            subject = subject,
            providerId = providerId,
            status = status,
            errorMessage = errorMessage,
            idempotencyKey = idempotencyKey,
            tags = tags,
            formLinkId = formLinkId,
            sentAt = sentAt ?: LocalDateTime.now(),
            updatedAt = updatedAt ?: LocalDateTime.now(),
        )
}
