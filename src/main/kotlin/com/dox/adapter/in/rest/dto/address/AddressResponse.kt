package com.dox.adapter.`in`.rest.dto.address

data class AddressResponse(
    val zipCode: String,
    val street: String,
    val neighborhood: String,
    val city: String,
    val state: String,
)
