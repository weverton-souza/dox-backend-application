package com.dox.domain.enum

object CustomerLabels {
    private val DEFAULTS = mapOf(Vertical.HEALTH to "Paciente")
    private const val FALLBACK = "Cliente"

    fun resolve(
        vertical: Vertical,
        override: String?,
    ): String = override?.takeIf { it.isNotBlank() } ?: DEFAULTS[vertical] ?: FALLBACK
}
