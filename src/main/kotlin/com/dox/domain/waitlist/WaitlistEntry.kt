package com.dox.domain.waitlist

data class WaitlistEntry(
    val name: String,
    val email: String,
    val profession: String,
    val city: String? = null,
)
