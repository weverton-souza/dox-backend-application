package com.dox.config

import com.dox.adapter.`in`.filter.JwtAuthenticationFilter
import com.dox.adapter.`in`.filter.MultiTenantFilter
import com.dox.adapter.`in`.filter.RateLimitFilter
import com.dox.adapter.`in`.filter.RequestSizeLimitFilter
import org.springframework.beans.factory.annotation.Value
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
import jakarta.servlet.DispatcherType
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val multiTenantFilter: MultiTenantFilter,
    private val rateLimitFilter: RateLimitFilter,
    private val requestSizeLimitFilter: RequestSizeLimitFilter,
    private val corsConfig: CorsConfig,
    @param:Value("\${SWAGGER_ENABLED:true}")
    private val swaggerEnabled: Boolean
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
            .headers {
                it.contentSecurityPolicy { csp -> csp.policyDirectives("default-src 'self'; frame-ancestors 'none'") }
                it.referrerPolicy { ref -> ref.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN) }
                it.permissionsPolicy { pp -> pp.policy("camera=(), microphone=(), geolocation=()") }
                if (!swaggerEnabled) {
                    it.httpStrictTransportSecurity { hsts ->
                        hsts.includeSubDomains(true)
                        hsts.maxAgeInSeconds(31536000)
                    }
                }
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                it.requestMatchers(
                    "/auth/register",
                    "/auth/login",
                    "/auth/refresh",
                    "/public/**",
                    "/actuator/health",
                    "/error"
                ).permitAll()
                if (swaggerEnabled) {
                    it.requestMatchers(
                        "/v3-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                    ).permitAll()
                }
                it.anyRequest().authenticated()
            }
            .addFilterBefore(requestSizeLimitFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterAfter(multiTenantFilter, JwtAuthenticationFilter::class.java)
            .build()
    }
}
