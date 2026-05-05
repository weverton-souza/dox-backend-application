package com.dox.application.port.output

import com.dox.domain.model.Address

interface AddressLookupPort {
    fun lookup(cep: String): Address?
}
