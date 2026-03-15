package com.dox.adapter.`in`.rest.dto.professional

import java.util.UUID

data class ProfessionalRequest(
    val name: String? = null,
    val crp: String? = null,
    val specialization: String? = null,
    val phone: String? = null,
    val instagram: String? = null,
    val email: String? = null,
    val logo: String? = null,
    val contactItems: List<Map<String, Any?>> = emptyList()
)

data class ProfessionalResponse(
    val id: UUID?,
    val name: String,
    val crp: String?,
    val specialization: String,
    val phone: String?,
    val instagram: String?,
    val email: String?,
    val logo: String?,
    val contactItems: List<Map<String, Any?>>
)
