package com.dox.config

import com.dox.adapter.`in`.filter.JwtAuthenticationFilter
import com.dox.adapter.`in`.filter.MultiTenantFilter
import com.dox.adapter.`in`.filter.RateLimitFilter
import com.dox.adapter.`in`.filter.RequestSizeLimitFilter
import jakarta.servlet.DispatcherType
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter

private const val DOCS_CSP =
    "default-src 'self' data: blob:; " +
        "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
        "style-src 'self' 'unsafe-inline' https:; " +
        "img-src 'self' data: https:; " +
        "font-src 'self' data: https:; " +
        "connect-src 'self' https://proxy.scalar.com https://api.scalar.com; " +
        "frame-ancestors 'none'"

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val multiTenantFilter: MultiTenantFilter,
    private val rateLimitFilter: RateLimitFilter,
    private val requestSizeLimitFilter: RequestSizeLimitFilter,
    private val corsConfig: CorsConfig,
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
    @Order(1)
    @ConditionalOnProperty(name = ["scalar.enabled"], havingValue = "true")
    fun docsSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .securityMatcher("/scalar/**", "/v3-docs/**", "/scalar-assets/**")
            .cors { it.configurationSource(corsConfig.corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .headers { headers ->
                headers.contentSecurityPolicy { it.policyDirectives(DOCS_CSP) }
                headers.referrerPolicy { it.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN) }
            }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .build()
    }

    @Bean
    @Order(2)
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
            "/admin/auth/login",
            "/public/**",
            "/modules/catalog",
            "/bundles",
            "/bundles/*",
            "/webhooks/**",
            "/actuator/health",
            "/error",
            "/favicon.ico",
            "/favicon.svg",
        ).permitAll()
        auth.requestMatchers("/admin/**").hasAuthority("ADMIN")
        auth.anyRequest().authenticated()
    }
}
