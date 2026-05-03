package com.dox.application.service

import com.dox.application.port.input.PublicVerifyUseCase
import com.dox.application.port.output.PublishedReportPersistencePort
import com.dox.domain.report.PublishedReport
import org.springframework.stereotype.Service

@Service
class PublicVerifyServiceImpl(
    private val publishedReportPersistencePort: PublishedReportPersistencePort,
) : PublicVerifyUseCase {
    override fun verifyByCode(code: String): PublishedReport? = publishedReportPersistencePort.findByVerificationCode(code)
}
