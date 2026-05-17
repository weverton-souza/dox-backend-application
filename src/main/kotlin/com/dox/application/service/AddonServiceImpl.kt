package com.dox.application.service

import com.dox.application.port.input.AddonUseCase
import com.dox.application.port.output.AddonPersistencePort
import com.dox.domain.billing.Addon
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AddonServiceImpl(
    private val addonPersistencePort: AddonPersistencePort,
) : AddonUseCase {
    @Transactional(readOnly = true)
    override fun listActive(): List<Addon> = addonPersistencePort.findAll().filter { it.active }.sortedBy { it.sortOrder }

    @Transactional(readOnly = true)
    override fun getById(id: String): Addon? = addonPersistencePort.findById(id)?.takeIf { it.active }
}
