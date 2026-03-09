package com.dox.extensions

import java.util.UUID

fun UUID.toPlainString(): String = toString().replace("-", "")

fun String.toUUID(): UUID = UUID.fromString(
    if (contains("-")) this
    else "${substring(0, 8)}-${substring(8, 12)}-${substring(12, 16)}-${substring(16, 20)}-${substring(20)}"
)

fun String.isValidUUIDFormat(): Boolean =
    matches(Regex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", RegexOption.IGNORE_CASE))

fun String.isValidPlainUUID(): Boolean =
    matches(Regex("^[0-9a-f]{32}$", RegexOption.IGNORE_CASE))
