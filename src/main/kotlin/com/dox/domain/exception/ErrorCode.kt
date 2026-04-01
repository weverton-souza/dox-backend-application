package com.dox.domain.exception

enum class ErrorCode(
    val code: String,
    val title: String,
) {
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "Recurso não encontrado"),
    DUPLICATE_RESOURCE("DUPLICATE_RESOURCE", "Recurso duplicado"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Credenciais inválidas"),
    INVALID_TOKEN("INVALID_TOKEN", "Token inválido"),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Token expirado"),
    ACCESS_DENIED("ACCESS_DENIED", "Acesso negado"),
    BUSINESS_RULE_VIOLATION("BUSINESS_RULE_VIOLATION", "Violação de regra de negócio"),
    VALIDATION_ERROR("VALIDATION_ERROR", "Erro de validação"),
    INTERNAL_ERROR("INTERNAL_ERROR", "Erro interno do servidor"),
}
