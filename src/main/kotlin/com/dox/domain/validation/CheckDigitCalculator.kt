package com.dox.domain.validation

internal object CheckDigitCalculator {
    fun calculate(
        digits: String,
        count: Int,
        weights: IntArray,
    ): Int {
        var sum = 0
        for (i in 0 until count) {
            sum += digits[i].digitToInt() * weights[i]
        }
        val remainder = sum % 11
        return if (remainder < 2) 0 else 11 - remainder
    }
}
