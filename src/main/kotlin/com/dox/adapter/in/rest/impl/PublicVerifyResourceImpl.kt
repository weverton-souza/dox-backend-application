package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.verify.PublicVerifyResponse
import com.dox.adapter.`in`.rest.resource.PublicVerifyResource
import com.dox.application.port.input.PublicVerifyUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PublicVerifyResourceImpl(
    private val publicVerifyUseCase: PublicVerifyUseCase,
) : PublicVerifyResource {
    override fun verify(code: String): ResponseEntity<PublicVerifyResponse> {
        val published =
            publicVerifyUseCase.verifyByCode(code)
                ?: return responseEntity(PublicVerifyResponse(valid = false, reason = "not_found"))

        return responseEntity(
            PublicVerifyResponse(
                valid = true,
                verificationCode = published.verificationCode,
            ),
        )
    }
}
