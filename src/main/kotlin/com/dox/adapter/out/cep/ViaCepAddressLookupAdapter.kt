package com.dox.adapter.out.cep

import com.dox.application.port.output.AddressLookupPort
import com.dox.domain.model.Address
import feign.FeignException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ViaCepAddressLookupAdapter(
    private val viaCepClient: ViaCepClient,
) : AddressLookupPort {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun lookup(cep: String): Address? =
        try {
            val response = viaCepClient.fetch(cep)
            if (response.error == true) {
                null
            } else {
                Address(
                    zipCode = response.zipCode ?: cep,
                    street = response.street.orEmpty(),
                    neighborhood = response.neighborhood.orEmpty(),
                    city = response.city.orEmpty(),
                    state = response.state.orEmpty(),
                )
            }
        } catch (e: FeignException) {
            log.warn("ViaCEP lookup failed for cep={}: {}", cep, e.message)
            null
        }
}
