package com.dox.application.port.input

import com.dox.domain.billing.Addon

interface AddonUseCase {
    fun listActive(): List<Addon>

    fun getById(id: String): Addon?
}
