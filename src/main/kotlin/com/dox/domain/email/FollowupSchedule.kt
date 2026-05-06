package com.dox.domain.email

object FollowupSchedule {
    data class Step(
        val level: FollowupLevel,
        val dayOffset: Int,
    )

    fun forTtlHours(ttlHours: Long): List<Step> {
        val ttlDays = (ttlHours / HOURS_IN_DAY).toInt()
        return when {
            ttlDays <= 3 -> SHORT
            ttlDays <= 7 -> MEDIUM
            ttlDays <= 14 -> LONG
            else -> EXTRA_LONG
        }
    }

    private const val HOURS_IN_DAY = 24L

    private val SHORT =
        listOf(
            Step(FollowupLevel.SOFT, 1),
            Step(FollowupLevel.URGENT, 3),
        )

    private val MEDIUM =
        listOf(
            Step(FollowupLevel.SOFT, 2),
            Step(FollowupLevel.MEDIUM, 5),
            Step(FollowupLevel.URGENT, 7),
        )

    private val LONG =
        listOf(
            Step(FollowupLevel.SOFT, 5),
            Step(FollowupLevel.MEDIUM, 10),
            Step(FollowupLevel.URGENT, 14),
        )

    private val EXTRA_LONG =
        listOf(
            Step(FollowupLevel.SOFT, 10),
            Step(FollowupLevel.SOFT, 20),
            Step(FollowupLevel.MEDIUM, 25),
            Step(FollowupLevel.URGENT, 30),
        )
}
