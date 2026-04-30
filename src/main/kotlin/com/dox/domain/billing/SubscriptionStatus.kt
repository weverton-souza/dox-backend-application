package com.dox.domain.billing

enum class SubscriptionStatus {
    TRIAL,
    TRIAL_GRACE,
    ACTIVE,
    GRACE,
    SUSPENDED,
    CANCEL_PENDING,
    CANCELED,
}
