package com.dox.domain.billing

import com.dox.domain.exception.InvalidTransitionException

object SubscriptionStateMachine {
    private val TRANSITIONS: Map<Pair<SubscriptionStatus, SubscriptionEvent>, SubscriptionStatus> =
        mapOf(
            (SubscriptionStatus.TRIAL to SubscriptionEvent.SUBSCRIBE_WITH_METHOD) to SubscriptionStatus.ACTIVE,
            (SubscriptionStatus.TRIAL to SubscriptionEvent.TRIAL_ENDED_NO_METHOD) to SubscriptionStatus.TRIAL_GRACE,
            (SubscriptionStatus.TRIAL_GRACE to SubscriptionEvent.SUBSCRIBE_WITH_METHOD) to SubscriptionStatus.ACTIVE,
            (SubscriptionStatus.TRIAL_GRACE to SubscriptionEvent.GRACE_EXPIRED) to SubscriptionStatus.SUSPENDED,
            (SubscriptionStatus.ACTIVE to SubscriptionEvent.PAYMENT_FAILED) to SubscriptionStatus.GRACE,
            (SubscriptionStatus.ACTIVE to SubscriptionEvent.CANCEL) to SubscriptionStatus.CANCEL_PENDING,
            (SubscriptionStatus.GRACE to SubscriptionEvent.PAYMENT_SUCCEEDED) to SubscriptionStatus.ACTIVE,
            (SubscriptionStatus.GRACE to SubscriptionEvent.GRACE_EXPIRED) to SubscriptionStatus.SUSPENDED,
            (SubscriptionStatus.GRACE to SubscriptionEvent.CANCEL) to SubscriptionStatus.CANCEL_PENDING,
            (SubscriptionStatus.SUSPENDED to SubscriptionEvent.PAYMENT_SUCCEEDED) to SubscriptionStatus.ACTIVE,
            (SubscriptionStatus.SUSPENDED to SubscriptionEvent.CANCEL) to SubscriptionStatus.CANCEL_PENDING,
            (SubscriptionStatus.CANCEL_PENDING to SubscriptionEvent.PERIOD_ENDED) to SubscriptionStatus.CANCELED,
            (SubscriptionStatus.CANCEL_PENDING to SubscriptionEvent.REACTIVATE) to SubscriptionStatus.ACTIVE,
            (SubscriptionStatus.CANCELED to SubscriptionEvent.REACTIVATE) to SubscriptionStatus.ACTIVE,
        )

    fun transition(
        current: SubscriptionStatus,
        event: SubscriptionEvent,
    ): SubscriptionStatus = TRANSITIONS[current to event] ?: throw InvalidTransitionException(current, event)

    fun canTransition(
        current: SubscriptionStatus,
        event: SubscriptionEvent,
    ): Boolean = (current to event) in TRANSITIONS

    fun allowedEvents(current: SubscriptionStatus): Set<SubscriptionEvent> = TRANSITIONS.keys.filter { it.first == current }.map { it.second }.toSet()
}
