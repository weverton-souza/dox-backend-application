package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.waitlist.WaitlistRequest
import com.dox.adapter.`in`.rest.dto.waitlist.WaitlistResponse
import com.dox.adapter.`in`.rest.resource.WaitlistResource
import com.dox.application.port.input.WaitlistUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class WaitlistResourceImpl(
    private val waitlistUseCase: WaitlistUseCase,
) : WaitlistResource {
    override fun join(request: WaitlistRequest): ResponseEntity<WaitlistResponse> {
        waitlistUseCase.join(
            name = request.name,
            email = request.email,
            profession = request.profession,
            city = request.city,
        )
        return responseEntity(WaitlistResponse(message = "Cadastro realizado com sucesso"))
    }
}
