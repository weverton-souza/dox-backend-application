package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.WaitlistJpaEntity
import com.dox.adapter.out.persistence.repository.WaitlistJpaRepository
import org.springframework.stereotype.Component

@Component
class WaitlistPersistenceAdapter(
    private val repository: WaitlistJpaRepository,
) {
    fun saveIfNotExists(entity: WaitlistJpaEntity): Boolean {
        if (repository.existsByEmail(entity.email)) return false
        repository.save(entity)
        return true
    }
}
