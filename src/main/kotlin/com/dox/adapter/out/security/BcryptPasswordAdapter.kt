package com.dox.adapter.out.security

import com.dox.application.port.output.PasswordEncoderPort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class BcryptPasswordAdapter(
    private val passwordEncoder: PasswordEncoder
) : PasswordEncoderPort {

    override fun encode(rawPassword: String): String = passwordEncoder.encode(rawPassword)

    override fun matches(rawPassword: String, encodedPassword: String): Boolean =
        passwordEncoder.matches(rawPassword, encodedPassword)
}
