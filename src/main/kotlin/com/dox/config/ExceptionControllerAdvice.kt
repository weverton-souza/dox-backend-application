package com.dox.config

import com.dox.domain.exception.AccessDeniedException
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.DomainException
import com.dox.domain.exception.DuplicateResourceException
import com.dox.domain.exception.ErrorCode
import com.dox.domain.exception.InvalidCredentialsException
import com.dox.domain.exception.InvalidTokenException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.exception.TokenExpiredException
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI
import java.time.Instant
import java.util.UUID

private fun buildProblemDetail(
    status: HttpStatusCode,
    title: String,
    detail: String,
    errorCode: String
): ProblemDetail {
    val pd = ProblemDetail.forStatusAndDetail(status, detail)
    pd.title = title
    pd.type = URI.create("urn:dox:error:$errorCode")
    pd.setProperty("errorCode", errorCode)
    pd.setProperty("timestamp", Instant.now())
    return pd
}

@RestControllerAdvice
@Order(1)
class DomainExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(DomainException::class)
    fun handle(ex: DomainException, request: WebRequest): ProblemDetail {
        logger.warn("[{}] {}", ex.errorCode.code, ex.message)

        val httpStatus = resolveHttpStatus(ex)
        val pd = buildProblemDetail(httpStatus, ex.errorCode.title, ex.message!!, ex.errorCode.code)

        when (ex) {
            is ResourceNotFoundException -> {
                pd.setProperty("resource", ex.resource)
                ex.identifier?.let { pd.setProperty("identifier", it) }
            }
            is DuplicateResourceException -> {
                pd.setProperty("field", ex.field)
                pd.setProperty("value", ex.value)
            }
            is InvalidCredentialsException,
            is InvalidTokenException,
            is TokenExpiredException,
            is AccessDeniedException,
            is BusinessException -> {}
        }

        return pd
    }

    private fun resolveHttpStatus(ex: DomainException): HttpStatus = when (ex) {
        is ResourceNotFoundException -> HttpStatus.NOT_FOUND
        is DuplicateResourceException -> HttpStatus.CONFLICT
        is InvalidCredentialsException -> HttpStatus.UNAUTHORIZED
        is InvalidTokenException -> HttpStatus.UNAUTHORIZED
        is TokenExpiredException -> HttpStatus.UNAUTHORIZED
        is AccessDeniedException -> HttpStatus.FORBIDDEN
        is BusinessException -> HttpStatus.UNPROCESSABLE_ENTITY
    }
}

@RestControllerAdvice
@Order(2)
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        log.warn("Erro ao ler requisição: {}", ex.message?.substringBefore("\n"))
        val pd = buildProblemDetail(
            HttpStatus.BAD_REQUEST,
            "Requisição inválida",
            "Corpo da requisição inválido ou malformado",
            "INVALID_REQUEST_BODY"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val pd = buildProblemDetail(status, ErrorCode.VALIDATION_ERROR.title, "Erro de validação", ErrorCode.VALIDATION_ERROR.code)

        val fieldErrors = ex.bindingResult.fieldErrors.map { fieldError ->
            mapOf(
                "field" to fieldError.field,
                "message" to (fieldError.defaultMessage ?: "Valor inválido"),
                "rejectedValue" to fieldError.rejectedValue
            )
        }
        pd.setProperty("errors", fieldErrors)

        return ResponseEntity.status(status).body(pd)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(ex: IllegalStateException): ProblemDetail {
        log.warn("IllegalStateException: {}", ex.message)
        return buildProblemDetail(
            org.springframework.http.HttpStatus.BAD_REQUEST,
            "Estado inválido",
            ex.message ?: "Estado inválido",
            "ILLEGAL_STATE"
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ProblemDetail {
        val traceId = UUID.randomUUID().toString()
        log.error("Erro inesperado [traceId={}]", traceId, ex)

        val pd = buildProblemDetail(
            org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
            ErrorCode.INTERNAL_ERROR.title,
            "Erro inesperado. Referência: $traceId",
            ErrorCode.INTERNAL_ERROR.code
        )
        pd.setProperty("traceId", traceId)
        return pd
    }
}
