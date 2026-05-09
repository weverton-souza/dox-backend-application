CREATE TABLE bundle_prices (
    id                       UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    bundle_id                VARCHAR(50)  NOT NULL REFERENCES bundles(id),
    price_monthly_cents      INT          NOT NULL,
    price_yearly_cents       INT          NOT NULL,
    seats_included           INT          NOT NULL,
    tracking_slots_included  INT          NOT NULL,
    valid_from               TIMESTAMP    NOT NULL DEFAULT NOW(),
    valid_until              TIMESTAMP,
    notes                    TEXT,
    created_by_user_id       UUID,
    created_at               TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_bundle_prices_current ON bundle_prices(bundle_id) WHERE valid_until IS NULL;
CREATE INDEX idx_bundle_prices_history ON bundle_prices(bundle_id, valid_from DESC);

INSERT INTO bundle_prices (
    bundle_id,
    price_monthly_cents,
    price_yearly_cents,
    seats_included,
    tracking_slots_included,
    valid_from,
    notes
)
SELECT
    id,
    price_monthly_cents,
    price_yearly_cents,
    seats_included,
    tracking_slots_included,
    created_at,
    'Snapshot inicial migrado de bundles'
FROM bundles;

ALTER TABLE subscriptions ADD COLUMN bundle_price_id UUID REFERENCES bundle_prices(id);

CREATE INDEX idx_subscriptions_bundle_price ON subscriptions(bundle_price_id) WHERE bundle_price_id IS NOT NULL;
