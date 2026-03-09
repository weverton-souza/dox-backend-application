package com.dox.extensions

import com.dox.domain.exception.ResourceNotFoundException
import java.util.Optional

fun <T> Optional<T>.orThrowNotFound(message: String = "Recurso não encontrado"): T =
    orElseThrow { ResourceNotFoundException(message) }

fun <T> Optional<T>.orNull(): T? = orElse(null)
