package com.dox.adapter.out.email

import com.dox.application.port.output.EmailPort
import com.dox.domain.email.EmailMessage
import com.dox.domain.email.EmailSendResult
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component
class EmailAdapterRouter(
    @Qualifier("resendEmailAdapter") private val resendAdapter: EmailPort,
) : EmailPort {
    override fun send(message: EmailMessage): EmailSendResult = resendAdapter.send(message)

    override fun isEnabled(): Boolean = resendAdapter.isEnabled()
}
