package com.dox.application.port.input

interface WaitlistUseCase {
    fun join(
        name: String,
        email: String,
        profession: String,
        city: String?,
    ): Boolean
}
