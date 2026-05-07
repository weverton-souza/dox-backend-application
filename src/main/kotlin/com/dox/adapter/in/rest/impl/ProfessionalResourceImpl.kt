package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.professional.ProfessionalRequest
import com.dox.adapter.`in`.rest.dto.professional.ProfessionalResponse
import com.dox.adapter.`in`.rest.resource.ProfessionalResource
import com.dox.application.port.input.ProfessionalUseCase
import com.dox.application.port.input.UpdateProfessionalCommand
import com.dox.application.port.output.TenantPersistencePort
import com.dox.domain.enum.CustomerLabels
import com.dox.domain.enum.Vertical
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.ProfessionalSettings
import com.dox.shared.ContextHolder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfessionalResourceImpl(
    private val professionalUseCase: ProfessionalUseCase,
    private val tenantPersistencePort: TenantPersistencePort,
) : ProfessionalResource {
    override fun get(): ResponseEntity<ProfessionalResponse> = responseEntity(professionalUseCase.get().toResponse())

    override fun update(request: ProfessionalRequest): ResponseEntity<ProfessionalResponse> =
        responseEntity(
            professionalUseCase.update(
                UpdateProfessionalCommand(
                    name = request.name,
                    socialName = request.socialName,
                    gender = request.gender,
                    crp = request.crp,
                    councilType = request.councilType,
                    councilNumber = request.councilNumber,
                    councilState = request.councilState,
                    specialization = request.specialization,
                    bio = request.bio,
                    addressCity = request.addressCity,
                    addressState = request.addressState,
                    phone = request.phone,
                    instagram = request.instagram,
                    email = request.email,
                    logo = request.logo,
                    contactItems = request.contactItems,
                    customerLabelOverride = request.customerLabelOverride,
                ),
            ).toResponse(),
        )

    private fun ProfessionalSettings.toResponse(): ProfessionalResponse {
        val vertical = currentTenantVertical()
        return ProfessionalResponse(
            id = id,
            name = name,
            socialName = socialName,
            gender = gender,
            crp = crp,
            councilType = councilType,
            councilNumber = councilNumber,
            councilState = councilState,
            specialization = specialization,
            bio = bio,
            addressCity = addressCity,
            addressState = addressState,
            phone = phone,
            instagram = instagram,
            email = email,
            logo = logo,
            contactItems = contactItems,
            customerLabel = CustomerLabels.resolve(vertical, customerLabelOverride),
            customerLabelOverride = customerLabelOverride,
        )
    }

    private fun currentTenantVertical(): Vertical {
        val tenantId = ContextHolder.getTenantIdOrThrow()
        val tenant =
            tenantPersistencePort.findById(tenantId)
                ?: throw ResourceNotFoundException("Tenant", tenantId.toString())
        return tenant.vertical
    }
}
