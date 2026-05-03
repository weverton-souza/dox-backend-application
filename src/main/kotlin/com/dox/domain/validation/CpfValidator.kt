package com.dox.domain.validation

object CpfValidator {
    private val ALL_SAME_DIGITS = (0..9).map { d -> d.toString().repeat(11) }.toSet()

    fun isValidCpf(value: String): Boolean {
        val digits = value.replace(Regex("[^0-9]"), "")

        if (digits.length != 11) return false
        if (digits in ALL_SAME_DIGITS) return false

        val d1 = CheckDigitCalculator.calculate(digits, 9, intArrayOf(10, 9, 8, 7, 6, 5, 4, 3, 2))
        if (d1 != digits[9].digitToInt()) return false

        val d2 = CheckDigitCalculator.calculate(digits, 10, intArrayOf(11, 10, 9, 8, 7, 6, 5, 4, 3, 2))
        return d2 == digits[10].digitToInt()
    }
}
