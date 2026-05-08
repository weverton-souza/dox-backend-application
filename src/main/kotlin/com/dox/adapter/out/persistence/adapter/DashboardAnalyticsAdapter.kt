package com.dox.adapter.out.persistence.adapter

import com.dox.application.port.output.DashboardAnalyticsPort
import com.dox.domain.billing.BillingType
import com.dox.domain.billing.MethodRevenue
import com.dox.domain.billing.ModuleRevenue
import com.dox.domain.billing.OverdueSummary
import com.dox.domain.billing.RecentSignup
import com.dox.domain.billing.RevenuePoint
import com.dox.domain.billing.SubscriptionStatus
import com.dox.domain.enum.Vertical
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID

@Component
class DashboardAnalyticsAdapter(
    private val jdbc: NamedParameterJdbcTemplate,
) : DashboardAnalyticsPort {
    override fun calculateMrrCents(asOf: LocalDateTime): Long {
        val subscriptionMrr =
            jdbc.queryForObject(
                """
                SELECT COALESCE(SUM(
                    CASE billing_cycle
                        WHEN 'MONTHLY' THEN value_cents
                        WHEN 'QUARTERLY' THEN value_cents / 3
                        WHEN 'SEMIANNUALLY' THEN value_cents / 6
                        WHEN 'YEARLY' THEN value_cents / 12
                        ELSE 0
                    END
                ), 0)
                FROM subscriptions
                WHERE status IN ('ACTIVE','GRACE','CANCEL_PENDING')
                  AND created_at <= :asOf
                  AND (canceled_at IS NULL OR canceled_at > :asOf)
                """.trimIndent(),
                MapSqlParameterSource("asOf", Timestamp.valueOf(asOf)),
                Long::class.java,
            ) ?: 0L

        val addonMrr =
            jdbc.queryForObject(
                """
                SELECT COALESCE(SUM(final_price_cents), 0)
                FROM tenant_addons
                WHERE activated_at <= :asOf
                  AND (canceled_at IS NULL OR canceled_at > :asOf)
                """.trimIndent(),
                MapSqlParameterSource("asOf", Timestamp.valueOf(asOf)),
                Long::class.java,
            ) ?: 0L

        return subscriptionMrr + addonMrr
    }

    override fun countActiveSubscriptions(asOf: LocalDateTime): Long =
        jdbc.queryForObject(
            """
            SELECT COUNT(*)
            FROM subscriptions
            WHERE status = 'ACTIVE'
              AND created_at <= :asOf
              AND (canceled_at IS NULL OR canceled_at > :asOf)
            """.trimIndent(),
            MapSqlParameterSource("asOf", Timestamp.valueOf(asOf)),
            Long::class.java,
        ) ?: 0L

    override fun countTrialSubscriptions(asOf: LocalDateTime): Long =
        jdbc.queryForObject(
            """
            SELECT COUNT(*)
            FROM subscriptions
            WHERE status IN ('TRIAL','TRIAL_GRACE')
              AND created_at <= :asOf
              AND (trial_end IS NULL OR trial_end > :asOf)
            """.trimIndent(),
            MapSqlParameterSource("asOf", Timestamp.valueOf(asOf)),
            Long::class.java,
        ) ?: 0L

    override fun countSignupsBetween(
        from: LocalDateTime,
        to: LocalDateTime,
    ): Long =
        jdbc.queryForObject(
            """
            SELECT COUNT(*)
            FROM tenants
            WHERE created_at >= :from AND created_at < :to
            """.trimIndent(),
            MapSqlParameterSource()
                .addValue("from", Timestamp.valueOf(from))
                .addValue("to", Timestamp.valueOf(to)),
            Long::class.java,
        ) ?: 0L

    override fun overdueSummary(): OverdueSummary {
        val overdueRow =
            jdbc.queryForMap(
                """
                SELECT
                    COALESCE(SUM(amount_cents), 0) AS amount,
                    COUNT(*)                       AS qty
                FROM payments
                WHERE status = 'OVERDUE' AND refunded_at IS NULL
                """.trimIndent(),
                MapSqlParameterSource(),
            )

        val statusCounts =
            jdbc.queryForList(
                """
                SELECT status, COUNT(*) AS qty
                FROM subscriptions
                WHERE status IN ('GRACE','SUSPENDED')
                GROUP BY status
                """.trimIndent(),
                MapSqlParameterSource(),
            ).associate { (it["status"] as String) to (it["qty"] as Number).toLong() }

        return OverdueSummary(
            overdueAmountCents = (overdueRow["amount"] as Number).toLong(),
            overdueCount = (overdueRow["qty"] as Number).toLong(),
            graceCount = statusCounts["GRACE"] ?: 0L,
            suspendedCount = statusCounts["SUSPENDED"] ?: 0L,
        )
    }

    override fun revenueByMonth(monthsBack: Int): List<RevenuePoint> =
        jdbc.query(
            """
            SELECT
                EXTRACT(YEAR  FROM paid_at)::int AS y,
                EXTRACT(MONTH FROM paid_at)::int AS m,
                COALESCE(SUM(amount_cents), 0)   AS total
            FROM payments
            WHERE paid_at IS NOT NULL
              AND refunded_at IS NULL
              AND paid_at >= NOW() - make_interval(months => :months)
            GROUP BY y, m
            ORDER BY y, m
            """.trimIndent(),
            MapSqlParameterSource("months", monthsBack),
        ) { rs, _ ->
            RevenuePoint(
                year = rs.getInt("y"),
                month = rs.getInt("m"),
                totalCents = rs.getLong("total"),
            )
        }

    override fun revenueByMethodBetween(
        from: LocalDateTime,
        to: LocalDateTime,
    ): List<MethodRevenue> =
        jdbc.query(
            """
            SELECT
                billing_type                   AS bt,
                COALESCE(SUM(amount_cents), 0) AS total,
                COUNT(*)                       AS qty
            FROM payments
            WHERE paid_at IS NOT NULL
              AND refunded_at IS NULL
              AND paid_at >= :from
              AND paid_at <  :to
            GROUP BY billing_type
            ORDER BY total DESC
            """.trimIndent(),
            MapSqlParameterSource()
                .addValue("from", Timestamp.valueOf(from))
                .addValue("to", Timestamp.valueOf(to)),
        ) { rs, _ ->
            MethodRevenue(
                billingType = BillingType.valueOf(rs.getString("bt")),
                totalCents = rs.getLong("total"),
                paymentCount = rs.getLong("qty"),
            )
        }

    override fun topModulesByMonthlyRevenue(limit: Int): List<ModuleRevenue> =
        jdbc.query(
            """
            SELECT
                module_id,
                COALESCE(SUM(final_price_cents), 0) AS mrr,
                COUNT(*)                            AS qty
            FROM tenant_modules
            WHERE status = 'ACTIVE'
              AND canceled_at IS NULL
            GROUP BY module_id
            ORDER BY mrr DESC
            LIMIT :lim
            """.trimIndent(),
            MapSqlParameterSource("lim", limit),
        ) { rs, _ ->
            ModuleRevenue(
                moduleId = rs.getString("module_id"),
                mrrCents = rs.getLong("mrr"),
                activeCount = rs.getLong("qty"),
            )
        }

    override fun recentSignups(limit: Int): List<RecentSignup> =
        jdbc.query(
            """
            SELECT
                t.id          AS tenant_id,
                t.name        AS tenant_name,
                t.vertical    AS vertical,
                t.created_at  AS created_at,
                s.status      AS subscription_status
            FROM tenants t
            LEFT JOIN subscriptions s ON s.tenant_id = t.id
            ORDER BY t.created_at DESC
            LIMIT :lim
            """.trimIndent(),
            MapSqlParameterSource("lim", limit),
        ) { rs, _ ->
            RecentSignup(
                tenantId = rs.getObject("tenant_id", UUID::class.java),
                tenantName = rs.getString("tenant_name"),
                vertical = Vertical.valueOf(rs.getString("vertical")),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                subscriptionStatus = rs.getString("subscription_status")?.let { SubscriptionStatus.valueOf(it) },
            )
        }
}
