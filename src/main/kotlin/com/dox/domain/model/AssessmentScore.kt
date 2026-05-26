package com.dox.domain.model

data class AssessmentScore(
    val index: String,
    val label: String,
    val value: String,
    val classification: String? = null,
)
