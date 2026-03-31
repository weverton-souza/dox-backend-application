package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.professional.ProfessionalRequest
import com.dox.adapter.`in`.rest.dto.professional.ProfessionalResponse
import com.dox.adapter.`in`.rest.resource.ProfessionalResource
import com.dox.application.port.input.ProfessionalUseCase
import com.dox.application.port.input.UpdateProfessionalCommand
import com.dox.domain.model.ProfessionalSettings
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfessionalResourceImpl(
    private val professionalUseCase: ProfessionalUseCase
) : ProfessionalResource {
    override fun get(): ResponseEntity<ProfessionalResponse> =
        responseEntity(professionalUseCase.get().toResponse())

    override fun update(request: ProfessionalRequest): ResponseEntity<ProfessionalResponse> =
        responseEntity(
            professionalUseCase.update(
                UpdateProfessionalCommand(
                    name = request.name,
                    crp = request.crp,
                    specialization = request.specialization,
                    phone = request.phone,
                    instagram = request.instagram,
                    email = request.email,
                    logo = request.logo,
                    contactItems = request.contactItems
                )
            ).toResponse()
        )

    private fun ProfessionalSettings.toResponse() = ProfessionalResponse(
        id, name, crp, specialization, phone, instagram, email, logo, contactItems
    )
}
