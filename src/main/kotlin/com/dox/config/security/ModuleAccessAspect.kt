package com.dox.config.security

import com.dox.application.port.input.ModuleAccessUseCase
import com.dox.domain.billing.AccessLevel
import com.dox.domain.exception.AccessDeniedException
import com.dox.shared.ContextHolder
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.lang.reflect.Method

@Aspect
@Component
class ModuleAccessAspect(
    private val moduleAccessUseCase: ModuleAccessUseCase,
) {
    @Around(
        "@within(com.dox.config.security.RequiresModule) || " +
            "@annotation(com.dox.config.security.RequiresModule)",
    )
    fun checkAccess(joinPoint: ProceedingJoinPoint): Any? {
        val method = (joinPoint.signature as MethodSignature).method
        val annotation = resolveAnnotation(method, joinPoint.target.javaClass) ?: return joinPoint.proceed()

        val tenantId = ContextHolder.getTenantIdOrThrow()
        val level = moduleAccessUseCase.getAccessLevel(tenantId, annotation.value)

        return when (level) {
            AccessLevel.FULL -> joinPoint.proceed()
            AccessLevel.READ_ONLY -> {
                if (isReadOperation(method)) {
                    joinPoint.proceed()
                } else {
                    throw AccessDeniedException(
                        "Módulo '${annotation.value}' está em modo somente leitura. Atualize seu plano para escrever.",
                    )
                }
            }
            AccessLevel.BLOCKED -> throw AccessDeniedException(
                "Módulo '${annotation.value}' não está ativo no seu plano.",
            )
        }
    }

    private fun resolveAnnotation(
        method: Method,
        targetClass: Class<*>,
    ): RequiresModule? =
        method.getAnnotation(RequiresModule::class.java)
            ?: targetClass.getAnnotation(RequiresModule::class.java)

    private fun isReadOperation(method: Method): Boolean {
        if (method.isAnnotationPresent(GetMapping::class.java)) return true
        if (method.isAnnotationPresent(PostMapping::class.java)) return false
        if (method.isAnnotationPresent(PutMapping::class.java)) return false
        if (method.isAnnotationPresent(DeleteMapping::class.java)) return false
        if (method.isAnnotationPresent(PatchMapping::class.java)) return false
        val requestMapping = method.getAnnotation(RequestMapping::class.java)
        if (requestMapping != null) {
            val methods = requestMapping.method
            return methods.isEmpty() || methods.all { it == RequestMethod.GET }
        }
        return false
    }
}
