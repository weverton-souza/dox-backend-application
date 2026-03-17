package com.dox.extensions

import java.time.LocalDateTime

fun LocalDateTime.isExpired(): Boolean = LocalDateTime.now().isAfter(this)
