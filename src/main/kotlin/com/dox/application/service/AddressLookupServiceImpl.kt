package com.dox.application.service

import com.dox.application.port.input.AddressLookupUseCase
import com.dox.application.port.output.AddressLookupPort
import com.dox.domain.model.Address
import org.springframework.stereotype.Service

@Service
class AddressLookupServiceImpl(
    private val addressLookupPort: AddressLookupPort,
) : AddressLookupUseCase {
    override fun byCep(cep: String): Address? {
        val digits = cep.filter { it.isDigit() }
        if (digits.length != 8) return null
        return addressLookupPort.lookup(digits)
    }
}
