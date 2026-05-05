package com.dox.domain.email

data class EmailMessage(
    val to: List<String>,
    val subject: String,
    val html: String,
    val text: String? = null,
    val from: String? = null,
    val replyTo: String? = null,
    val cc: List<String> = emptyList(),
    val bcc: List<String> = emptyList(),
    val tags: Map<String, String> = emptyMap(),
    val idempotencyKey: String? = null,
)
