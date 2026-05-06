package com.dox.adapter.`in`.scheduler

import com.dox.application.port.output.AuthTokenPort
import com.dox.application.port.output.CustomerPersistencePort
import com.dox.application.port.output.FormLinkFollowupPersistencePort
import com.dox.application.port.output.FormLinkPersistencePort
import com.dox.application.port.output.FormPersistencePort
import com.dox.application.port.output.ProfessionalSettingsPersistencePort
import com.dox.application.port.output.TenantPersistencePort
import com.dox.application.service.FormFollowupEmailRequestedEvent
import com.dox.domain.email.FollowupLevel
import com.dox.domain.enum.FormLinkFollowupStatus
import com.dox.domain.enum.FormLinkStatus
import com.dox.domain.enum.RespondentType
import com.dox.domain.model.Customer
import com.dox.domain.model.CustomerContact
import com.dox.domain.model.FormLink
import com.dox.domain.model.FormLinkFollowup
import com.dox.shared.TenantContext
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Component
class FormFollowupJob(
    private val tenantPersistencePort: TenantPersistencePort,
    private val formLinkFollowupPersistencePort: FormLinkFollowupPersistencePort,
    private val formLinkPersistencePort: FormLinkPersistencePort,
    private val formPersistencePort: FormPersistencePort,
    private val customerPersistencePort: CustomerPersistencePort,
    private val professionalSettingsPersistencePort: ProfessionalSettingsPersistencePort,
    private val authTokenPort: AuthTokenPort,
    private val eventPublisher: ApplicationEventPublisher,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 0 9 * * *")
    fun dispatchFollowups() {
        val now = LocalDateTime.now()
        val tenants = tenantPersistencePort.findAll()
        var totalSent = 0
        var totalSkipped = 0

        tenants.forEach { tenant ->
            try {
                TenantContext.withTenantContext(tenant.id) {
                    val (sent, skipped) = processTenant(tenant.id, now)
                    totalSent += sent
                    totalSkipped += skipped
                }
            } catch (e: Exception) {
                log.error("Falha ao processar followups do tenant {}: {}", tenant.id, e.message, e)
            }
        }

        if (totalSent > 0 || totalSkipped > 0) {
            log.info("Followups dispatched: sent={}, skipped={}", totalSent, totalSkipped)
        }
    }

    @Transactional
    fun processTenant(
        tenantId: UUID,
        now: LocalDateTime,
    ): Pair<Int, Int> {
        val due = formLinkFollowupPersistencePort.findScheduledDueBefore(now)
        if (due.isEmpty()) return 0 to 0

        var sent = 0
        var skipped = 0

        due.forEach { followup ->
            val link = formLinkPersistencePort.findById(followup.formLinkId)
            if (link == null) {
                markSkipped(followup, "form-link-not-found")
                skipped++
                return@forEach
            }

            if (link.status != FormLinkStatus.PENDING) {
                markSkipped(followup, "link-status-${link.status.name.lowercase()}")
                skipped++
                return@forEach
            }

            if (link.isExpired()) {
                markSkipped(followup, "link-expired")
                skipped++
                return@forEach
            }

            val customer = customerPersistencePort.findById(link.customerId)
            if (customer == null) {
                markSkipped(followup, "customer-not-found")
                skipped++
                return@forEach
            }

            val contact = link.customerContactId?.let { customerPersistencePort.findContactById(it) }
            val recipient = resolveRecipientEmail(link, customer, contact)
            if (recipient == null) {
                markSkipped(followup, "no-email")
                skipped++
                return@forEach
            }

            val formWithVersion =
                runCatching { formPersistencePort.findFormById(link.formId) }.getOrNull()
            if (formWithVersion == null) {
                markSkipped(followup, "form-not-found")
                skipped++
                return@forEach
            }

            val professional = professionalSettingsPersistencePort.find()
            val professionalName = professional?.name?.ifBlank { "Profissional" } ?: "Profissional"
            val formVersion =
                formPersistencePort.findVersionById(link.formVersionId)
            val formTitle = formVersion?.title ?: "questionário"
            val token = authTokenPort.generateFormLinkToken(tenantId, link.id, link.expiresAt)

            val respondentFirstName =
                when (link.respondentType) {
                    RespondentType.CUSTOMER -> customer.displayName()?.substringBefore(' ') ?: "olá"
                    RespondentType.CONTACT -> contact?.name?.substringBefore(' ') ?: "olá"
                    RespondentType.PROFESSIONAL -> "olá"
                }

            eventPublisher.publishEvent(
                FormFollowupEmailRequestedEvent(
                    tenantId = tenantId,
                    formLinkId = link.id,
                    followupId = followup.id,
                    level = followup.level,
                    dayOffset = followup.dayOffset,
                    recipient = recipient,
                    respondentFirstName = respondentFirstName,
                    isAboutCustomer = link.respondentType == RespondentType.CONTACT,
                    customerName = customer.displayName(),
                    professionalName = professionalName,
                    formTitle = formTitle,
                    formToken = token,
                    expiresAt = link.expiresAt,
                ),
            )

            formLinkFollowupPersistencePort.save(
                followup.copy(
                    status = FormLinkFollowupStatus.SENT,
                    sentAt = now,
                ),
            )
            sent++
        }

        return sent to skipped
    }

    private fun markSkipped(
        followup: FormLinkFollowup,
        reason: String,
    ) {
        formLinkFollowupPersistencePort.save(
            followup.copy(
                status = FormLinkFollowupStatus.SKIPPED,
                errorMessage = reason,
                sentAt = LocalDateTime.now(),
            ),
        )
    }

    private fun resolveRecipientEmail(
        link: FormLink,
        customer: Customer,
        contact: CustomerContact?,
    ): String? =
        when (link.respondentType) {
            RespondentType.CUSTOMER -> (customer.data["email"] as? String)?.trim()?.ifBlank { null }
            RespondentType.CONTACT -> contact?.email?.trim()?.ifBlank { null }
            RespondentType.PROFESSIONAL -> null
        }

    @Suppress("unused")
    private val supportedLevels = setOf(FollowupLevel.SOFT, FollowupLevel.MEDIUM, FollowupLevel.URGENT)
}
