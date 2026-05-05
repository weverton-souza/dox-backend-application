package com.dox.application.port.output

import com.dox.domain.email.EmailSuppression

interface EmailSuppressionPersistencePort {
    fun save(suppression: EmailSuppression): EmailSuppression

    fun isSuppressed(email: String): Boolean

    fun findByEmail(email: String): EmailSuppression?

    fun delete(email: String): Boolean
}
