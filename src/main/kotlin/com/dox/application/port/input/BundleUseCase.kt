package com.dox.application.port.input

import com.dox.domain.billing.Bundle
import com.dox.domain.billing.Module

interface BundleUseCase {
    fun listActive(): List<Bundle>

    fun getById(id: String): Bundle?

    fun expandToModules(bundleId: String): Set<Module>
}
