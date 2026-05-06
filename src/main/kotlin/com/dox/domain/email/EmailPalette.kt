package com.dox.domain.email

data class EmailPalette(
    val hero: String,
    val heroEyebrow: String,
    val heroSubtle: String,
    val ctaBackground: String,
    val heroTextColor: String = "#FFFFFF",
    val ctaTextColor: String = "#FFFFFF",
)
