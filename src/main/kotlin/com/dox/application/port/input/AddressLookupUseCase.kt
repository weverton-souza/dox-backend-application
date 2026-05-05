package com.dox.application.port.input

import com.dox.domain.model.Address

interface AddressLookupUseCase {
    fun byCep(cep: String): Address?
}
