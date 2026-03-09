package com.dox.shared

import java.util.UUID

data class Context(
    val tenantId: UUID? = null,
    val userId: UUID? = null
)

class ContextHolder {
    companion object {
        private val contextThreadLocal = ThreadLocal<Context>()

        var context: Context
            get() = contextThreadLocal.get() ?: Context()
            set(value) = contextThreadLocal.set(value)

        fun clear() = contextThreadLocal.remove()
    }
}
