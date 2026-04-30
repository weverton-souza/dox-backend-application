package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.ProfessionalSettingsJpaEntity
import com.dox.adapter.out.persistence.repository.ProfessionalSettingsJpaRepository
import com.dox.application.port.output.ProfessionalSettingsPersistencePort
import com.dox.domain.model.ProfessionalSettings
import org.springframework.stereotype.Component

@Component
class ProfessionalSettingsPersistenceAdapter(
    private val repository: ProfessionalSettingsJpaRepository,
) : ProfessionalSettingsPersistencePort {
    override fun find(): ProfessionalSettings? = repository.findFirstByOrderByCreatedAtAsc()?.toDomain()

    override fun save(settings: ProfessionalSettings): ProfessionalSettings {
        val entity =
            repository.findFirstByOrderByCreatedAtAsc()
                ?: ProfessionalSettingsJpaEntity()
        entity.name = settings.name
        entity.socialName = settings.socialName
        entity.gender = settings.gender
        entity.crp = settings.crp
        entity.councilType = settings.councilType
        entity.councilNumber = settings.councilNumber
        entity.councilState = settings.councilState
        entity.specialization = settings.specialization
        entity.bio = settings.bio
        entity.addressCity = settings.addressCity
        entity.addressState = settings.addressState
        entity.phone = settings.phone
        entity.instagram = settings.instagram
        entity.email = settings.email
        entity.logo = settings.logo
        entity.contactItems = settings.contactItems
        return repository.save(entity).toDomain()
    }

    private fun ProfessionalSettingsJpaEntity.toDomain() =
        ProfessionalSettings(
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
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
