package com.dox.domain.email

data class RenderedEmail(
    val subject: String,
    val html: String,
    val text: String,
)
