package com.dox.domain.billing

enum class SubscriptionEvent {
    SUBSCRIBE_WITH_METHOD,
    TRIAL_ENDED_NO_METHOD,
    PAYMENT_SUCCEEDED,
    PAYMENT_FAILED,
    GRACE_EXPIRED,
    CANCEL,
    PERIOD_ENDED,
    REACTIVATE,
}
