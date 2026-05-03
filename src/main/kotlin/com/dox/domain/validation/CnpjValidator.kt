package com.dox.domain.validation

object CnpjValidator {
    private val ALL_SAME_DIGITS = (0..9).map { d -> d.toString().repeat(14) }.toSet()

    fun isValidCnpj(value: String): Boolean {
        val digits = value.replace(Regex("[^0-9]"), "")

        if (digits.length != 14) return false
        if (digits in ALL_SAME_DIGITS) return false

        val d1 = CheckDigitCalculator.calculate(digits, 12, intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2))
        if (d1 != digits[12].digitToInt()) return false

        val d2 = CheckDigitCalculator.calculate(digits, 13, intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2))
        return d2 == digits[13].digitToInt()
    }
}
