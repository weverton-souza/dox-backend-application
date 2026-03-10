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
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI
import java.time.Instant
import java.util.UUID

@RestControllerAdvice
@Order(1)
class DomainExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(DomainException::class)
    fun handle(ex: DomainException, request: WebRequest): ProblemDetail {
        logger.warn("[{}] {}", ex.errorCode.code, ex.message)

        val pd = ProblemDetail.forStatusAndDetail(ex.httpStatus, ex.message!!)
        pd.title = ex.errorCode.title
        pd.type = URI.create("urn:dox:error:${ex.errorCode.code}")
        pd.setProperty("errorCode", ex.errorCode.code)
        pd.setProperty("timestamp", Instant.now())

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
}

@RestControllerAdvice
@Order(2)
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val pd = ProblemDetail.forStatusAndDetail(status, "Erro de validação")
        pd.title = ErrorCode.VALIDATION_ERROR.title
        pd.type = URI.create("urn:dox:error:${ErrorCode.VALIDATION_ERROR.code}")
        pd.setProperty("errorCode", ErrorCode.VALIDATION_ERROR.code)
        pd.setProperty("timestamp", Instant.now())

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

        val pd = ProblemDetail.forStatusAndDetail(
            org.springframework.http.HttpStatus.BAD_REQUEST,
            ex.message ?: "Estado inválido"
        )
        pd.title = "Estado inválido"
        pd.type = URI.create("urn:dox:error:ILLEGAL_STATE")
        pd.setProperty("errorCode", "ILLEGAL_STATE")
        pd.setProperty("timestamp", Instant.now())
        return pd
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ProblemDetail {
        val traceId = UUID.randomUUID().toString()
        log.error("Erro inesperado [traceId={}]", traceId, ex)

        val pd = ProblemDetail.forStatusAndDetail(
            org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
            "Erro inesperado. Referência: $traceId"
        )
        pd.title = ErrorCode.INTERNAL_ERROR.title
        pd.type = URI.create("urn:dox:error:${ErrorCode.INTERNAL_ERROR.code}")
        pd.setProperty("errorCode", ErrorCode.INTERNAL_ERROR.code)
        pd.setProperty("traceId", traceId)
        pd.setProperty("timestamp", Instant.now())
        return pd
    }
}
