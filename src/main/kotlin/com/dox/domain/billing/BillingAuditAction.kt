package com.dox.domain.billing

enum class BillingAuditAction {
    GRANT_MODULE,
    EXTEND_TRIAL,
    LOCK_PRICE,
    UNLOCK_PRICE,
    EDIT_MODULE_PRICE,
    EDIT_BUNDLE,
    EDIT_ADDON,
}
