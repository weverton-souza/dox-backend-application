package com.dox.application.service

import com.dox.application.port.input.BundleUseCase
import com.dox.application.port.output.BundlePersistencePort
import com.dox.domain.billing.Bundle
import com.dox.domain.billing.Module
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import org.springframework.stereotype.Service

@Service
class BundleServiceImpl(
    private val persistencePort: BundlePersistencePort,
) : BundleUseCase {
    override fun listActive(): List<Bundle> = persistencePort.findAllActive()

    override fun getById(id: String): Bundle? = persistencePort.findById(id)

    override fun expandToModules(bundleId: String): Set<Module> {
        val bundle = persistencePort.findById(bundleId) ?: throw ResourceNotFoundException("Bundle", bundleId)
        return bundle.modules.map { id ->
            Module.fromId(id) ?: throw BusinessException("Bundle '$bundleId' referencia módulo inválido: '$id'")
        }.toSet()
    }
}
