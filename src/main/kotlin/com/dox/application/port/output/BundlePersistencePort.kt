package com.dox.application.port.output

import com.dox.domain.billing.Bundle

interface BundlePersistencePort {
    fun findAllActive(): List<Bundle>

    fun findAll(): List<Bundle>

    fun findById(id: String): Bundle?

    fun save(bundle: Bundle): Bundle
}
