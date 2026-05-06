package com.dox.domain.email

object EmailPalettes {
    private val DEFAULT =
        EmailPalette(
            hero = "#0984E3",
            heroEyebrow = "#A8C8F0",
            heroSubtle = "#D4E4F4",
            ctaBackground = "#0984E3",
        )

    private val SECURITY =
        EmailPalette(
            hero = "#6C5CE7",
            heroEyebrow = "#C4BFF0",
            heroSubtle = "#D8D2F5",
            ctaBackground = "#6C5CE7",
        )

    private val SUCCESS =
        EmailPalette(
            hero = "#00B894",
            heroEyebrow = "#A0F0CF",
            heroSubtle = "#C8F5E0",
            ctaBackground = "#00B894",
        )

    private val FOLLOWUP_SOFT =
        EmailPalette(
            hero = "#00CEC9",
            heroEyebrow = "#A0F0EE",
            heroSubtle = "#D0F5F3",
            ctaBackground = "#00CEC9",
        )

    private val FOLLOWUP_MEDIUM =
        EmailPalette(
            hero = "#6C5CE7",
            heroEyebrow = "#C4BFF0",
            heroSubtle = "#D8D2F5",
            ctaBackground = "#6C5CE7",
        )

    private val FOLLOWUP_URGENT =
        EmailPalette(
            hero = "#E17055",
            heroEyebrow = "#F5C8B5",
            heroSubtle = "#F0DAC8",
            ctaBackground = "#E17055",
        )

    fun resolve(template: EmailTemplateId): EmailPalette =
        when (template.category) {
            EmailCategory.DEFAULT -> DEFAULT
            EmailCategory.SECURITY -> SECURITY
            EmailCategory.SUCCESS -> SUCCESS
            EmailCategory.FOLLOWUP ->
                when (template.followupLevel) {
                    FollowupLevel.SOFT -> FOLLOWUP_SOFT
                    FollowupLevel.MEDIUM -> FOLLOWUP_MEDIUM
                    FollowupLevel.URGENT -> FOLLOWUP_URGENT
                    null -> DEFAULT
                }
        }
}
