package com.dox.domain.email

import java.time.LocalDateTime

data class EmailSendResult(
    val providerId: String?,
    val sentAt: LocalDateTime,
    val accepted: Boolean,
    val errorMessage: String? = null,
)
