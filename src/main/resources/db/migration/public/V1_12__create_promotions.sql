CREATE TABLE promotions (
    id                       UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    code                     VARCHAR(60)  UNIQUE,
    name                     VARCHAR(150) NOT NULL,
    type                     VARCHAR(30)  NOT NULL CHECK (type IN (
                                              'COUPON','BUNDLE','GRANT','REFERRAL','LOYALTY','WINBACK',
                                              'CAMPAIGN','PARTNER','TRIAL_EXTENSION','VOLUME_DISCOUNT',
                                              'CROSS_SELL','ANNIVERSARY')),
    discount_type            VARCHAR(30)  NOT NULL CHECK (discount_type IN (
                                              'PERCENTAGE','FIXED_AMOUNT','FREE_MONTHS','TRIAL_EXTENSION_DAYS')),
    discount_value           INT          NOT NULL,
    duration_type            VARCHAR(20)  NOT NULL CHECK (duration_type IN ('ONCE','FOREVER','FIXED_MONTHS')),
    duration_months          INT,
    max_redemptions          INT,
    current_redemptions      INT          NOT NULL DEFAULT 0,
    valid_from               TIMESTAMP,
    valid_until              TIMESTAMP,
    applies_to               VARCHAR(30)  NOT NULL DEFAULT 'ALL_MODULES' CHECK (applies_to IN (
                                              'ALL_MODULES','SPECIFIC_MODULES','MIN_BUNDLE','FIRST_PAYMENT_ONLY')),
    applies_to_modules       JSONB,
    applies_to_verticals     JSONB,
    applies_to_signup_after  TIMESTAMP,
    applies_to_signup_before TIMESTAMP,
    stackable_with           JSONB        NOT NULL DEFAULT '[]'::jsonb,
    skip_proration           BOOLEAN      NOT NULL DEFAULT FALSE,
    requires_approval        BOOLEAN      NOT NULL DEFAULT FALSE,
    auto_apply_event         VARCHAR(60),
    partner_id               UUID,
    next_promotion_id        UUID         REFERENCES promotions(id),
    created_at               TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by_user_id       UUID,
    archived_at              TIMESTAMP,
    CONSTRAINT redemptions_within_limit CHECK (max_redemptions IS NULL OR current_redemptions <= max_redemptions)
);

CREATE INDEX idx_promotions_code ON promotions(code) WHERE code IS NOT NULL;
CREATE INDEX idx_promotions_active ON promotions(archived_at) WHERE archived_at IS NULL;

CREATE TABLE tenant_promotions (
    id                  UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID         NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    promotion_id        UUID         NOT NULL REFERENCES promotions(id),
    applied_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    expires_at          TIMESTAMP,
    applied_by_user_id  UUID,
    source_event        VARCHAR(60),
    status              VARCHAR(20)  NOT NULL CHECK (status IN ('ACTIVE','EXPIRED','REVOKED')),
    notes               TEXT,
    UNIQUE (tenant_id, promotion_id)
);

CREATE INDEX idx_tenant_promotions_active ON tenant_promotions(tenant_id, status) WHERE status = 'ACTIVE';
CREATE INDEX idx_tenant_promotions_expires ON tenant_promotions(expires_at) WHERE status = 'ACTIVE' AND expires_at IS NOT NULL;
