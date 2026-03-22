package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.ProfessionalSettingsJpaEntity
import com.dox.adapter.out.persistence.repository.ProfessionalSettingsJpaRepository
import com.dox.application.port.output.ProfessionalSettingsPersistencePort
import com.dox.domain.model.ProfessionalSettings
import org.springframework.stereotype.Component

@Component
class ProfessionalSettingsPersistenceAdapter(
    private val repository: ProfessionalSettingsJpaRepository
) : ProfessionalSettingsPersistencePort {

    override fun find(): ProfessionalSettings? =
        repository.findFirstByOrderByCreatedAtAsc()?.toDomain()

    override fun save(settings: ProfessionalSettings): ProfessionalSettings {
        val entity = repository.findFirstByOrderByCreatedAtAsc()
            ?: ProfessionalSettingsJpaEntity()
        entity.name = settings.name
        entity.crp = settings.crp
        entity.specialization = settings.specialization
        entity.phone = settings.phone
        entity.instagram = settings.instagram
        entity.email = settings.email
        entity.logo = settings.logo
        entity.contactItems = settings.contactItems
        return repository.save(entity).toDomain()
    }

    private fun ProfessionalSettingsJpaEntity.toDomain() = ProfessionalSettings(
        id = id,
        name = name,
        crp = crp,
        specialization = specialization,
        phone = phone,
        instagram = instagram,
        email = email,
        logo = logo,
        contactItems = contactItems,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
