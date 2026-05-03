package com.dox.application.port.input

import com.dox.domain.report.PublishedReport

interface PublicVerifyUseCase {
    fun verifyByCode(code: String): PublishedReport?
}
