package com.dox.application.port.output

import com.dox.domain.email.EmailMessage
import com.dox.domain.email.EmailSendResult

interface EmailPort {
    fun send(message: EmailMessage): EmailSendResult

    fun isEnabled(): Boolean
}
