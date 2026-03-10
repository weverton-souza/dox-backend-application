package com.dox.domain.exception

import org.springframework.http.HttpStatus

sealed class DomainException(
    message: String,
    val errorCode: ErrorCode,
    val httpStatus: HttpStatus
) : RuntimeException(message)

class ResourceNotFoundException(
    val resource: String,
    val identifier: String? = null
) : DomainException(
    message = if (identifier != null) "$resource com id $identifier não encontrado" else "$resource não encontrado",
    errorCode = ErrorCode.RESOURCE_NOT_FOUND,
    httpStatus = HttpStatus.NOT_FOUND
)

class DuplicateResourceException(
    val field: String,
    val value: String
) : DomainException(
    message = "$field '$value' já cadastrado",
    errorCode = ErrorCode.DUPLICATE_RESOURCE,
    httpStatus = HttpStatus.CONFLICT
)

class InvalidCredentialsException : DomainException(
    message = "Credenciais inválidas",
    errorCode = ErrorCode.INVALID_CREDENTIALS,
    httpStatus = HttpStatus.UNAUTHORIZED
)

class InvalidTokenException(
    detail: String = "Token inválido"
) : DomainException(
    message = detail,
    errorCode = ErrorCode.INVALID_TOKEN,
    httpStatus = HttpStatus.UNAUTHORIZED
)

class TokenExpiredException : DomainException(
    message = "Token expirado",
    errorCode = ErrorCode.TOKEN_EXPIRED,
    httpStatus = HttpStatus.UNAUTHORIZED
)

class AccessDeniedException(
    detail: String = "Acesso negado"
) : DomainException(
    message = detail,
    errorCode = ErrorCode.ACCESS_DENIED,
    httpStatus = HttpStatus.FORBIDDEN
)

class BusinessException(
    detail: String
) : DomainException(
    message = detail,
    errorCode = ErrorCode.BUSINESS_RULE_VIOLATION,
    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
)
