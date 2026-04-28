package com.dox.shared

import com.dox.domain.exception.BusinessException
import java.util.UUID

data class Context(
    val tenantId: UUID? = null,
    val userId: UUID? = null,
    val ipAddress: String? = null,
    val userAgent: String? = null,
)

class ContextHolder {
    companion object {
        private val contextThreadLocal = ThreadLocal<Context>()

        var context: Context
            get() = contextThreadLocal.get() ?: Context()
            set(value) = contextThreadLocal.set(value)

        fun setUserId(userId: UUID) {
            context = context.copy(userId = userId)
        }

        fun setTenantId(tenantId: UUID) {
            context = context.copy(tenantId = tenantId)
        }

        fun clear() = contextThreadLocal.remove()

        fun getUserIdOrThrow(): UUID = context.userId ?: throw IllegalStateException("Usuário não autenticado")

        fun getTenantIdOrThrow(): UUID = context.tenantId ?: throw BusinessException("Tenant não identificado")

        fun getIpAddress(): String? = context.ipAddress

        fun getUserAgent(): String? = context.userAgent
    }
}
