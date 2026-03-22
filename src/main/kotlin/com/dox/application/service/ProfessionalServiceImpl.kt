package com.dox.application.service

import com.dox.application.port.input.ProfessionalUseCase
import com.dox.application.port.input.UpdateProfessionalCommand
import com.dox.application.port.output.ProfessionalSettingsPersistencePort
import com.dox.domain.model.ProfessionalSettings
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfessionalServiceImpl(
    private val persistencePort: ProfessionalSettingsPersistencePort
) : ProfessionalUseCase {

    override fun get(): ProfessionalSettings =
        persistencePort.find() ?: ProfessionalSettings()

    @Transactional
    override fun update(command: UpdateProfessionalCommand): ProfessionalSettings {
        val current = persistencePort.find() ?: ProfessionalSettings()
        val updated = current.copy(
            name = command.name ?: "",
            crp = command.crp,
            specialization = command.specialization ?: "",
            phone = command.phone,
            instagram = command.instagram,
            email = command.email,
            logo = command.logo,
            contactItems = command.contactItems
        )
        return persistencePort.save(updated)
    }
}
