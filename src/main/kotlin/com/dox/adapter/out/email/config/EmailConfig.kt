package com.dox.adapter.out.email.config

import com.resend.Resend
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(EmailProperties::class)
class EmailConfig(
    private val properties: EmailProperties,
) {
    @Bean
    fun resendClient(): Resend = Resend(properties.apiKey.ifBlank { "re_disabled" })
}
