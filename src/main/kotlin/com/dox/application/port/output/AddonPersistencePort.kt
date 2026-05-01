package com.dox.application.port.output

import com.dox.domain.billing.Addon

interface AddonPersistencePort {
    fun findAll(): List<Addon>

    fun findById(id: String): Addon?

    fun save(addon: Addon): Addon
}
