package com.dox.domain.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [CnpjValidator::class])
annotation class ValidCnpj(
    val message: String = "CNPJ inválido",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class CnpjValidator : ConstraintValidator<ValidCnpj, String> {

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value.isNullOrBlank()) return true
        return isValidCnpj(value)
    }

    companion object {
        private val ALL_SAME_DIGITS = (0..9).map { d -> d.toString().repeat(14) }.toSet()

        fun isValidCnpj(value: String): Boolean {
            val digits = value.replace(Regex("[^0-9]"), "")

            if (digits.length != 14) return false
            if (digits in ALL_SAME_DIGITS) return false

            val d1 = calculateDigit(digits, 0, 12, intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2))
            if (d1 != digits[12].digitToInt()) return false

            val d2 = calculateDigit(digits, 0, 13, intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2))
            return d2 == digits[13].digitToInt()
        }

        private fun calculateDigit(digits: String, start: Int, count: Int, weights: IntArray): Int {
            var sum = 0
            for (i in 0 until count) {
                sum += digits[start + i].digitToInt() * weights[i]
            }
            val remainder = sum % 11
            return if (remainder < 2) 0 else 11 - remainder
        }
    }
}
