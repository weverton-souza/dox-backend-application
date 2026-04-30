package com.dox.domain.exception

import com.dox.domain.billing.SubscriptionEvent
import com.dox.domain.billing.SubscriptionStatus

class InvalidTransitionException(
    val from: SubscriptionStatus,
    val event: SubscriptionEvent,
) : BusinessException(detail = "Transição inválida: '${from.name}' não aceita evento '${event.name}'")
