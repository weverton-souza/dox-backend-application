package com.dox.application.service

import com.dox.application.port.input.EmailUseCase
import com.dox.application.port.input.SendFormFollowupEmailCommand
import com.dox.shared.TenantContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDateTime
import java.util.UUID

data class FormFollowupEmailRequestedEvent(
    val tenantId: UUID,
    val formLinkId: UUID,
    val followupId: UUID,
    val level: com.dox.domain.email.FollowupLevel,
    val dayOffset: Int,
    val recipient: String,
    val respondentFirstName: String,
    val isAboutCustomer: Boolean,
    val customerName: String?,
    val professionalName: String,
    val formTitle: String,
    val formToken: String,
    val expiresAt: LocalDateTime,
)

@Component
class FormFollowupEmailEventListener(
    private val emailUseCase: EmailUseCase,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener
    fun handle(event: FormFollowupEmailRequestedEvent) {
        try {
            TenantContext.withTenantContext(event.tenantId) {
                emailUseCase.sendFormFollowup(
                    SendFormFollowupEmailCommand(
                        tenantId = event.tenantId,
                        formLinkId = event.formLinkId,
                        followupId = event.followupId,
                        level = event.level,
                        dayOffset = event.dayOffset,
                        recipient = event.recipient,
                        respondentFirstName = event.respondentFirstName,
                        isAboutCustomer = event.isAboutCustomer,
                        customerName = event.customerName,
                        professionalName = event.professionalName,
                        formTitle = event.formTitle,
                        formToken = event.formToken,
                        expiresAt = event.expiresAt,
                    ),
                )
            }
        } catch (e: Exception) {
            log.error(
                "Falha ao enviar form followup (level={}, formLinkId={}, recipient={}): {}",
                event.level,
                event.formLinkId,
                event.recipient,
                e.message,
                e,
            )
        }
    }
}
