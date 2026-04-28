package com.dox.adapter.`in`.rest.dto.verify

import java.time.LocalDateTime

data class PublicVerifyResponse(
    val valid: Boolean,
    val verificationCode: String? = null,
    val finalizedAt: LocalDateTime? = null,
    val professional: ProfessionalInfo? = null,
    val customerInitials: String? = null,
    val reason: String? = null,
)

data class ProfessionalInfo(
    val name: String?,
    val crp: String?,
)
