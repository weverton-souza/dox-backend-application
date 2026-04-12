package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.waitlist.WaitlistRequest
import com.dox.adapter.`in`.rest.dto.waitlist.WaitlistResponse
import com.dox.adapter.`in`.rest.resource.WaitlistResource
import com.dox.adapter.out.persistence.adapter.WaitlistPersistenceAdapter
import com.dox.adapter.out.persistence.entity.WaitlistJpaEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class WaitlistResourceImpl(
    private val waitlistAdapter: WaitlistPersistenceAdapter,
) : WaitlistResource {
    override fun join(request: WaitlistRequest): ResponseEntity<WaitlistResponse> {
        val entity =
            WaitlistJpaEntity(
                name = request.name,
                email = request.email.lowercase().trim(),
                profession = request.profession,
                city = request.city?.trim()?.ifBlank { null },
            )
        waitlistAdapter.saveIfNotExists(entity)
        return responseEntity(WaitlistResponse(message = "Cadastro realizado com sucesso"))
    }
}
