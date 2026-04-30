package com.dox.application.port.input

import com.dox.domain.model.ProfessionalSettings

data class UpdateProfessionalCommand(
    val name: String? = null,
    val socialName: String? = null,
    val gender: String? = null,
    val crp: String? = null,
    val councilType: String? = null,
    val councilNumber: String? = null,
    val councilState: String? = null,
    val specialization: String? = null,
    val bio: String? = null,
    val addressCity: String? = null,
    val addressState: String? = null,
    val phone: String? = null,
    val instagram: String? = null,
    val email: String? = null,
    val logo: String? = null,
    val contactItems: List<Map<String, Any?>> = emptyList(),
)

interface ProfessionalUseCase {
    fun get(): ProfessionalSettings

    fun update(command: UpdateProfessionalCommand): ProfessionalSettings
}
