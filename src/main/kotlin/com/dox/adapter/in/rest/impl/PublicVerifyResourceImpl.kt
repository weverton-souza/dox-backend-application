package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.verify.PublicVerifyResponse
import com.dox.adapter.`in`.rest.resource.PublicVerifyResource
import com.dox.adapter.out.persistence.adapter.PublishedReportPersistenceAdapter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PublicVerifyResourceImpl(
    private val publishedReportPersistenceAdapter: PublishedReportPersistenceAdapter,
) : PublicVerifyResource {
    override fun verify(code: String): ResponseEntity<PublicVerifyResponse> {
        val published =
            publishedReportPersistenceAdapter.findByVerificationCode(code)
                ?: return responseEntity(PublicVerifyResponse(valid = false, reason = "not_found"))

        return responseEntity(
            PublicVerifyResponse(
                valid = true,
                verificationCode = published.verificationCode,
            ),
        )
    }
}
