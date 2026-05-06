package com.dox.application.service

import com.dox.application.port.input.EmailUseCase
import com.dox.application.port.input.SendFormInviteEmailCommand
import com.dox.shared.TenantContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDateTime
import java.util.UUID

data class FormInviteEmailRequestedEvent(
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
    val expiresAt: LocalDateTime,
)

@Component
class FormInviteEmailEventListener(
    private val emailUseCase: EmailUseCase,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener
    fun handle(event: FormInviteEmailRequestedEvent) {
        try {
            TenantContext.withTenantContext(event.tenantId) {
                emailUseCase.sendFormInvite(
                    SendFormInviteEmailCommand(
                        tenantId = event.tenantId,
                        formLinkId = event.formLinkId,
                        recipient = event.recipient,
                        respondentName = event.respondentName,
                        isAboutCustomer = event.isAboutCustomer,
                        customerName = event.customerName,
                        professionalName = event.professionalName,
                        professionalCouncil = event.professionalCouncil,
                        formTitle = event.formTitle,
                        formToken = event.formToken,
                        expiresAt = event.expiresAt,
                    ),
                )
            }
        } catch (e: Exception) {
            log.error(
                "Falha ao enviar form-invite (formLinkId={}, recipient={}): {}",
                event.formLinkId,
                event.recipient,
                e.message,
                e,
            )
        }
    }
}
