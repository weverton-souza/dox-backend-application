package com.dox.application.service

import com.dox.application.port.input.WaitlistUseCase
import com.dox.application.port.output.WaitlistPersistencePort
import com.dox.domain.waitlist.WaitlistEntry
import org.springframework.stereotype.Service

@Service
class WaitlistServiceImpl(
    private val waitlistPersistencePort: WaitlistPersistencePort,
) : WaitlistUseCase {
    override fun join(
        name: String,
        email: String,
        profession: String,
        city: String?,
    ): Boolean {
        val entry =
            WaitlistEntry(
                name = name,
                email = email.lowercase().trim(),
                profession = profession,
                city = city?.trim()?.ifBlank { null },
            )
        return waitlistPersistencePort.saveIfNotExists(entry)
    }
}
