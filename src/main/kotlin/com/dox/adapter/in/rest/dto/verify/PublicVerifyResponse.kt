package com.dox.adapter.`in`.rest.dto.verify

data class PublicVerifyResponse(
    val valid: Boolean,
    val verificationCode: String? = null,
    val reason: String? = null,
)
