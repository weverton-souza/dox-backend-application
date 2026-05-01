package com.dox.domain.billing

enum class DiscountType(val applicationOrder: Int) {
    FIXED_AMOUNT(1),
    PERCENTAGE(2),
    FREE_MONTHS(3),
    TRIAL_EXTENSION_DAYS(4),
}
