package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.WaitlistJpaEntity
import com.dox.adapter.out.persistence.repository.WaitlistJpaRepository
import com.dox.application.port.output.WaitlistPersistencePort
import com.dox.domain.waitlist.WaitlistEntry
import org.springframework.stereotype.Component

@Component
class WaitlistPersistenceAdapter(
    private val repository: WaitlistJpaRepository,
) : WaitlistPersistencePort {
    override fun saveIfNotExists(entry: WaitlistEntry): Boolean {
        if (repository.existsByEmail(entry.email)) return false
        repository.save(
            WaitlistJpaEntity(
                name = entry.name,
                email = entry.email,
                profession = entry.profession,
                city = entry.city,
            ),
        )
        return true
    }
}
