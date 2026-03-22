package com.dox.application.port.output

import com.dox.domain.model.ProfessionalSettings

interface ProfessionalSettingsPersistencePort {
    fun find(): ProfessionalSettings?
    fun save(settings: ProfessionalSettings): ProfessionalSettings
}
