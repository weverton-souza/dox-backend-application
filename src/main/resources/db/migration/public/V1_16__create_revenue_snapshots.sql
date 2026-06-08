CREATE TABLE revenue_snapshots (
    id                       UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    year                     INT          NOT NULL,
    month                    INT          NOT NULL CHECK (month BETWEEN 1 AND 12),
    mrr_cents                BIGINT       NOT NULL,
    arr_cents                BIGINT       NOT NULL,
    active_subscriptions     INT          NOT NULL,
    trial_subscriptions      INT          NOT NULL,
    overdue_amount_cents     BIGINT       NOT NULL,
    new_signups              INT          NOT NULL,
    canceled_subscriptions   INT          NOT NULL,
    trial_started            INT          NOT NULL,
    trial_converted          INT          NOT NULL,
    captured_at              TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE (year, month)
);

CREATE INDEX idx_revenue_snapshots_year_month ON revenue_snapshots(year, month);
