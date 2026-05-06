package com.dox.application.service

import com.dox.adapter.out.email.config.EmailProperties
import com.dox.adapter.out.email.template.EmailTemplateRenderer
import com.dox.application.port.input.EmailUseCase
import com.dox.application.port.input.SendFormFollowupEmailCommand
import com.dox.application.port.input.SendFormInviteEmailCommand
import com.dox.application.port.input.SendTemplatedEmailCommand
import com.dox.application.port.input.SendWelcomeEmailCommand
import com.dox.application.port.input.TestEmailCommand
import com.dox.application.port.output.EmailLogPersistencePort
import com.dox.application.port.output.EmailPort
import com.dox.application.port.output.EmailSuppressionPersistencePort
import com.dox.domain.email.EmailLog
import com.dox.domain.email.EmailLogStatus
import com.dox.domain.email.EmailMessage
import com.dox.domain.email.EmailSuppression
import com.dox.domain.email.EmailSuppressionReason
import com.dox.domain.email.EmailTemplateId
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

@Service
class EmailServiceImpl(
    private val emailPort: EmailPort,
    private val templateRenderer: EmailTemplateRenderer,
    private val logPersistencePort: EmailLogPersistencePort,
    private val suppressionPersistencePort: EmailSuppressionPersistencePort,
    private val properties: EmailProperties,
) : EmailUseCase {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun sendTemplated(command: SendTemplatedEmailCommand): EmailLog {
        command.idempotencyKey?.let { key ->
            logPersistencePort.findByIdempotencyKey(key)?.let {
                log.info("Skipping duplicate send for idempotency key {}", key)
                return it
            }
        }

        if (suppressionPersistencePort.isSuppressed(command.recipient)) {
            log.info("Recipient {} is suppressed, skipping send", command.recipient)
            return persistLog(command, EmailLogStatus.SUPPRESSED, providerId = null, errorMessage = "suppressed")
        }

        val rendered = templateRenderer.render(command.templateId, command.subject, command.variables)

        val message =
            EmailMessage(
                to = listOf(command.recipient),
                subject = rendered.subject,
                html = rendered.html,
                text = rendered.text,
                tags = command.tags + mapOf("template" to command.templateId.templateName),
                idempotencyKey = command.idempotencyKey,
            )

        val result = emailPort.send(message)

        val status = if (result.accepted) EmailLogStatus.SENT else EmailLogStatus.FAILED
        return persistLog(command, status, result.providerId, result.errorMessage)
    }

    @Transactional
    override fun sendWelcome(command: SendWelcomeEmailCommand): EmailLog {
        val verifyUrl =
            "${properties.apiBaseUrl.trimEnd('/')}/auth/verify-email-redirect?token=" +
                URLEncoder.encode(command.verificationToken, StandardCharsets.UTF_8)

        return sendTemplated(
            SendTemplatedEmailCommand(
                templateId = EmailTemplateId.WELCOME,
                recipient = command.recipient,
                subject = "Bem-vindo(a) ao DOX, ${command.firstName}",
                variables =
                    mapOf(
                        "firstName" to command.firstName,
                        "verifyUrl" to verifyUrl,
                    ),
                idempotencyKey = "${command.userId}-WELCOME",
                tags = mapOf("user_id" to command.userId.toString()),
            ),
        )
    }

    @Transactional
    override fun sendFormInvite(command: SendFormInviteEmailCommand): EmailLog {
        val formUrl = "${properties.frontendUrl.trimEnd('/')}/public/forms/${command.formToken}"
        val expiresAtFormatted = command.expiresAt.format(EXPIRES_FORMATTER)

        val subject =
            if (command.isAboutCustomer) {
                "Questionário sobre ${command.customerName ?: "cliente"} — ${command.professionalName}"
            } else {
                "Você recebeu um questionário de ${command.professionalName}"
            }

        return sendTemplated(
            SendTemplatedEmailCommand(
                templateId = EmailTemplateId.FORM_INVITE,
                recipient = command.recipient,
                subject = subject,
                variables =
                    mapOf(
                        "respondentName" to command.respondentName,
                        "isAboutCustomer" to command.isAboutCustomer,
                        "customerName" to command.customerName,
                        "professionalName" to command.professionalName,
                        "professionalCouncil" to command.professionalCouncil,
                        "formTitle" to command.formTitle,
                        "formUrl" to formUrl,
                        "expiresAtFormatted" to expiresAtFormatted,
                    ),
                tenantId = command.tenantId,
                idempotencyKey = "${command.formLinkId}-FORM_INVITE",
                tags =
                    mapOf(
                        "form_link_id" to command.formLinkId.toString(),
                        "tenant_id" to command.tenantId.toString(),
                    ),
            ),
        )
    }

    @Transactional
    override fun sendFormFollowup(command: SendFormFollowupEmailCommand): EmailLog {
        val formUrl = "${properties.frontendUrl.trimEnd('/')}/public/forms/${command.formToken}"
        val expiresAtFormatted = command.expiresAt.format(EXPIRES_FORMATTER)
        val daysRemaining = java.time.Duration.between(java.time.LocalDateTime.now(), command.expiresAt).toDays().coerceAtLeast(0).toInt()

        val templateId =
            when (command.level) {
                com.dox.domain.email.FollowupLevel.SOFT -> EmailTemplateId.FORM_FOLLOWUP_SOFT
                com.dox.domain.email.FollowupLevel.MEDIUM -> EmailTemplateId.FORM_FOLLOWUP_MEDIUM
                com.dox.domain.email.FollowupLevel.URGENT -> EmailTemplateId.FORM_FOLLOWUP_URGENT
            }

        val subject =
            when (command.level) {
                com.dox.domain.email.FollowupLevel.SOFT -> "Lembrete: questionário de ${command.professionalName}"
                com.dox.domain.email.FollowupLevel.MEDIUM -> "Seu questionário expira em $daysRemaining dias"
                com.dox.domain.email.FollowupLevel.URGENT -> "Último dia — questionário expira hoje"
            }

        return sendTemplated(
            SendTemplatedEmailCommand(
                templateId = templateId,
                recipient = command.recipient,
                subject = subject,
                variables =
                    mapOf(
                        "respondentFirstName" to command.respondentFirstName,
                        "isAboutCustomer" to command.isAboutCustomer,
                        "customerName" to command.customerName,
                        "professionalName" to command.professionalName,
                        "formTitle" to command.formTitle,
                        "formUrl" to formUrl,
                        "expiresAtFormatted" to expiresAtFormatted,
                        "daysRemaining" to daysRemaining,
                    ),
                tenantId = command.tenantId,
                idempotencyKey = "${command.formLinkId}-FOLLOWUP-${command.level.name}-D${command.dayOffset}",
                tags =
                    mapOf(
                        "form_link_id" to command.formLinkId.toString(),
                        "followup_id" to command.followupId.toString(),
                        "followup_level" to command.level.name,
                        "tenant_id" to command.tenantId.toString(),
                    ),
            ),
        )
    }

    @Transactional
    override fun sendTest(command: TestEmailCommand): EmailLog {
        val defaults =
            mapOf(
                "firstName" to "Teste",
                "verifyUrl" to "${properties.apiBaseUrl.trimEnd('/')}/auth/verify-email-redirect?token=sample-token",
                "subject" to "[TESTE] Email de teste · ${command.templateId.templateName}",
            )
        return sendTemplated(
            SendTemplatedEmailCommand(
                templateId = command.templateId,
                recipient = command.recipient,
                subject = "[TESTE] ${command.templateId.templateName}",
                variables = defaults + command.sampleVariables,
                idempotencyKey = null,
                tags = mapOf("source" to "admin_test"),
            ),
        )
    }

    override fun listLogs(
        templateId: String?,
        status: EmailLogStatus?,
        recipientEmail: String?,
        tenantId: UUID?,
        pageable: Pageable,
    ): Page<EmailLog> = logPersistencePort.findPaginated(templateId, status, recipientEmail, tenantId, pageable)

    @Transactional
    override fun handleProviderEvent(
        providerId: String,
        eventType: String,
        errorMessage: String?,
    ) {
        val newStatus =
            when (eventType.lowercase()) {
                "email.delivered" -> EmailLogStatus.DELIVERED
                "email.bounced" -> EmailLogStatus.BOUNCED
                "email.complained" -> EmailLogStatus.COMPLAINED
                "email.opened" -> EmailLogStatus.OPENED
                "email.clicked" -> EmailLogStatus.CLICKED
                "email.delivery_delayed" -> EmailLogStatus.PENDING
                "email.failed" -> EmailLogStatus.FAILED
                else -> {
                    log.info("Unhandled Resend event type: {}", eventType)
                    return
                }
            }

        logPersistencePort.updateStatus(providerId, newStatus, errorMessage)

        if (newStatus == EmailLogStatus.BOUNCED || newStatus == EmailLogStatus.COMPLAINED) {
            val existing = logPersistencePort.findByProviderId(providerId) ?: return
            val reason =
                if (newStatus == EmailLogStatus.BOUNCED) {
                    EmailSuppressionReason.HARD_BOUNCE
                } else {
                    EmailSuppressionReason.COMPLAINT
                }
            suppressionPersistencePort.save(
                EmailSuppression(
                    email = existing.recipientEmail,
                    reason = reason,
                    notes = errorMessage ?: "auto from $eventType",
                ),
            )
        }
    }

    private fun persistLog(
        command: SendTemplatedEmailCommand,
        status: EmailLogStatus,
        providerId: String?,
        errorMessage: String?,
    ): EmailLog {
        val formLinkId =
            runCatching { command.tags["form_link_id"]?.let { UUID.fromString(it) } }.getOrNull()
        return logPersistencePort.save(
            EmailLog(
                tenantId = command.tenantId,
                templateId = command.templateId.templateName,
                recipientEmail = command.recipient,
                subject = command.subject,
                providerId = providerId,
                status = status,
                errorMessage = errorMessage,
                idempotencyKey = command.idempotencyKey,
                tags = command.tags,
                formLinkId = formLinkId,
            ),
        )
    }

    companion object {
        private val EXPIRES_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm", Locale.forLanguageTag("pt-BR"))
    }
}
