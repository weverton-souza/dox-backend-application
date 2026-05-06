package com.dox.domain.email

enum class EmailCategory {
    DEFAULT,
    SECURITY,
    SUCCESS,
    FOLLOWUP,
}

enum class FollowupLevel {
    SOFT,
    MEDIUM,
    URGENT,
}
