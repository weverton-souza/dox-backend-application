package com.dox.application.port.input

import com.dox.domain.email.EmailLog
import com.dox.domain.email.EmailLogStatus
import com.dox.domain.email.EmailTemplateId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

data class SendTemplatedEmailCommand(
    val templateId: EmailTemplateId,
    val recipient: String,
    val subject: String,
    val variables: Map<String, Any?>,
    val tenantId: UUID? = null,
    val idempotencyKey: String? = null,
    val tags: Map<String, String> = emptyMap(),
)

data class SendWelcomeEmailCommand(
    val userId: UUID,
    val firstName: String,
    val recipient: String,
    val verificationToken: String,
)

data class SendFormInviteEmailCommand(
    val tenantId: UUID,
    val formLinkId: UUID,
    val recipient: String,
    val respondentName: String,
    val isAboutCustomer: Boolean,
    val customerName: String?,
    val professionalName: String,
    val professionalCouncil: String?,
    val formTitle: String,
    val formToken: String,
    val expiresAt: java.time.LocalDateTime,
)

data class TestEmailCommand(
    val templateId: EmailTemplateId,
    val recipient: String,
    val sampleVariables: Map<String, Any?> = emptyMap(),
)

interface EmailUseCase {
    fun sendTemplated(command: SendTemplatedEmailCommand): EmailLog

    fun sendWelcome(command: SendWelcomeEmailCommand): EmailLog

    fun sendFormInvite(command: SendFormInviteEmailCommand): EmailLog

    fun sendTest(command: TestEmailCommand): EmailLog

    fun listLogs(
        templateId: String?,
        status: EmailLogStatus?,
        recipientEmail: String?,
        tenantId: UUID?,
        pageable: Pageable,
    ): Page<EmailLog>

    fun handleProviderEvent(
        providerId: String,
        eventType: String,
        errorMessage: String? = null,
    )
}
