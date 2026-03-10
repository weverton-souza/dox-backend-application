package com.dox.extensions

import com.dox.domain.exception.ResourceNotFoundException
import java.util.Optional

fun <T> Optional<T>.orThrowNotFound(resource: String = "Recurso", identifier: String? = null): T =
    orElseThrow { ResourceNotFoundException(resource, identifier) }

fun <T> Optional<T>.orNull(): T? = orElse(null)
