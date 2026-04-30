package com.dox.config

import com.dox.adapter.`in`.filter.JwtAuthenticationFilter
import com.dox.adapter.`in`.filter.MultiTenantFilter
import com.dox.adapter.`in`.filter.RateLimitFilter
import com.dox.adapter.`in`.filter.RequestSizeLimitFilter
import jakarta.servlet.DispatcherType
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
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val multiTenantFilter: MultiTenantFilter,
    private val rateLimitFilter: RateLimitFilter,
    private val requestSizeLimitFilter: RequestSizeLimitFilter,
    private val corsConfig: CorsConfig,
    @param:Value("\${SWAGGER_ENABLED:false}")
    private val swaggerEnabled: Boolean,
    @param:Value("\${dox.hsts.enabled:false}")
    private val hstsEnabled: Boolean,
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun jwtFilterRegistration(filter: JwtAuthenticationFilter): FilterRegistrationBean<JwtAuthenticationFilter> = FilterRegistrationBean(filter).apply { isEnabled = false }

    @Bean
    fun multiTenantFilterRegistration(filter: MultiTenantFilter): FilterRegistrationBean<MultiTenantFilter> = FilterRegistrationBean(filter).apply { isEnabled = false }

    @Bean
    fun rateLimitFilterRegistration(filter: RateLimitFilter): FilterRegistrationBean<RateLimitFilter> = FilterRegistrationBean(filter).apply { isEnabled = false }

    @Bean
    fun requestSizeLimitFilterRegistration(filter: RequestSizeLimitFilter): FilterRegistrationBean<RequestSizeLimitFilter> = FilterRegistrationBean(filter).apply { isEnabled = false }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors { it.configurationSource(corsConfig.corsConfigurationSource()) }
            .csrf { it.disable() }
            .headers { configureHeaders(it) }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { configureAuthorization(it) }
            .addFilterBefore(requestSizeLimitFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterAfter(multiTenantFilter, JwtAuthenticationFilter::class.java)
            .build()
    }

    private fun configureHeaders(headers: org.springframework.security.config.annotation.web.configurers.HeadersConfigurer<HttpSecurity>) {
        headers.contentSecurityPolicy { it.policyDirectives("default-src 'self'; frame-ancestors 'none'") }
        headers.referrerPolicy { it.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN) }
        headers.permissionsPolicy { it.policy("camera=(), microphone=(), geolocation=()") }
        if (hstsEnabled) {
            headers.httpStrictTransportSecurity { hsts ->
                hsts.includeSubDomains(true)
                hsts.maxAgeInSeconds(31536000)
            }
        }
    }

    private fun configureAuthorization(auth: org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) {
        auth.dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
        auth.requestMatchers(
            "/auth/register",
            "/auth/login",
            "/auth/refresh",
            "/public/**",
            "/modules/catalog",
            "/bundles",
            "/bundles/*",
            "/actuator/health",
            "/error",
        ).permitAll()
        if (swaggerEnabled) {
            auth.requestMatchers(
                "/v3-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
            ).permitAll()
        }
        auth.anyRequest().authenticated()
    }
}
