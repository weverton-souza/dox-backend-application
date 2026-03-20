package com.dox.config

import com.dox.adapter.`in`.filter.JwtAuthenticationFilter
import com.dox.adapter.`in`.filter.MultiTenantFilter
import com.dox.adapter.`in`.filter.RateLimitFilter
import com.dox.adapter.`in`.filter.RequestSizeLimitFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val multiTenantFilter: MultiTenantFilter,
    private val rateLimitFilter: RateLimitFilter,
    private val requestSizeLimitFilter: RequestSizeLimitFilter,
    private val corsConfig: CorsConfig
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun jwtFilterRegistration(filter: JwtAuthenticationFilter): FilterRegistrationBean<JwtAuthenticationFilter> =
        FilterRegistrationBean(filter).apply { isEnabled = false }

    @Bean
    fun multiTenantFilterRegistration(filter: MultiTenantFilter): FilterRegistrationBean<MultiTenantFilter> =
        FilterRegistrationBean(filter).apply { isEnabled = false }

    @Bean
    fun rateLimitFilterRegistration(filter: RateLimitFilter): FilterRegistrationBean<RateLimitFilter> =
        FilterRegistrationBean(filter).apply { isEnabled = false }

    @Bean
    fun requestSizeLimitFilterRegistration(filter: RequestSizeLimitFilter): FilterRegistrationBean<RequestSizeLimitFilter> =
        FilterRegistrationBean(filter).apply { isEnabled = false }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors { it.configurationSource(corsConfig.corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/auth/register",
                    "/auth/login",
                    "/auth/refresh",
                    "/public/**",
                    "/v3-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/actuator/health"
                ).permitAll()
                it.anyRequest().authenticated()
            }
            .addFilterBefore(requestSizeLimitFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterAfter(multiTenantFilter, JwtAuthenticationFilter::class.java)
            .build()
    }
}
