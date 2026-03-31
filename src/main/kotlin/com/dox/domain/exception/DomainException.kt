package com.dox.domain.exception

sealed class DomainException(
    message: String,
    val errorCode: ErrorCode
) : RuntimeException(message)

class ResourceNotFoundException(
    val resource: String,
    val identifier: String? = null
) : DomainException(
        message = if (identifier != null) "$resource com id $identifier não encontrado" else "$resource não encontrado",
        errorCode = ErrorCode.RESOURCE_NOT_FOUND
    )

class DuplicateResourceException(
    val field: String,
    val value: String
) : DomainException(
        message = "$field '$value' já cadastrado",
        errorCode = ErrorCode.DUPLICATE_RESOURCE
    )

class InvalidCredentialsException : DomainException(
    message = "Credenciais inválidas",
    errorCode = ErrorCode.INVALID_CREDENTIALS
)

class InvalidTokenException(
    detail: String = "Token inválido"
) : DomainException(
        message = detail,
        errorCode = ErrorCode.INVALID_TOKEN
    )

class TokenExpiredException : DomainException(
    message = "Token expirado",
    errorCode = ErrorCode.TOKEN_EXPIRED
)

class AccessDeniedException(
    detail: String = "Acesso negado"
) : DomainException(
        message = detail,
        errorCode = ErrorCode.ACCESS_DENIED
    )

class BusinessException(
    detail: String
) : DomainException(
        message = detail,
        errorCode = ErrorCode.BUSINESS_RULE_VIOLATION
    )
