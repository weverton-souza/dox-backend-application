package com.dox.application.port.output

import com.dox.domain.email.EmailLog
import com.dox.domain.email.EmailLogStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface EmailLogPersistencePort {
    fun save(log: EmailLog): EmailLog

    fun findById(id: UUID): EmailLog?

    fun findByProviderId(providerId: String): EmailLog?

    fun findByIdempotencyKey(key: String): EmailLog?

    fun findByFormLinkId(formLinkId: UUID): List<EmailLog>

    fun findPaginated(
        templateId: String?,
        status: EmailLogStatus?,
        recipientEmail: String?,
        tenantId: UUID?,
        pageable: Pageable,
    ): Page<EmailLog>

    fun updateStatus(
        providerId: String,
        status: EmailLogStatus,
        errorMessage: String? = null,
    ): Int
}
