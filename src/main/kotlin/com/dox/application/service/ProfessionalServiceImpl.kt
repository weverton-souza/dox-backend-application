package com.dox.application.service

import com.dox.adapter.out.persistence.entity.ProfessionalSettingsJpaEntity
import com.dox.adapter.out.persistence.repository.ProfessionalSettingsJpaRepository
import com.dox.application.port.input.ProfessionalUseCase
import com.dox.application.port.input.UpdateProfessionalCommand
import com.dox.domain.model.ProfessionalSettings
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfessionalServiceImpl(
    private val repository: ProfessionalSettingsJpaRepository
) : ProfessionalUseCase {

    override fun get(): ProfessionalSettings {
        val entity = repository.findAll().firstOrNull()
            ?: return ProfessionalSettings()
        return entity.toDomain()
    }

    @Transactional
    override fun update(command: UpdateProfessionalCommand): ProfessionalSettings {
        val entity = repository.findAll().firstOrNull()
            ?: ProfessionalSettingsJpaEntity()
        entity.name = command.name ?: ""
        entity.crp = command.crp
        entity.specialization = command.specialization ?: ""
        entity.phone = command.phone
        entity.instagram = command.instagram
        entity.email = command.email
        entity.logo = command.logo
        entity.contactItems = command.contactItems
        return repository.save(entity).toDomain()
    }

    private fun ProfessionalSettingsJpaEntity.toDomain() = ProfessionalSettings(
        id, name, crp, specialization, phone, instagram, email, logo, contactItems, createdAt, updatedAt
    )
}
