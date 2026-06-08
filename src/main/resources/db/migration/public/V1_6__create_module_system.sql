CREATE TABLE tenant_modules (
    id                UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id         UUID         NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    module_id         VARCHAR(50)  NOT NULL,
    status            VARCHAR(20)  NOT NULL CHECK (status IN ('TRIAL','ACTIVE','GRACE','SUSPENDED','CANCELED','GRANTED')),
    source            VARCHAR(20)  NOT NULL CHECK (source IN ('TRIAL','BUNDLE','INDIVIDUAL','GRANT','PROMOTION')),
    source_id         VARCHAR(255),
    activated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    expires_at        TIMESTAMP,
    grace_until       TIMESTAMP,
    base_price_cents  INT          NOT NULL DEFAULT 0,
    final_price_cents INT          NOT NULL DEFAULT 0,
    price_locked      BOOLEAN      NOT NULL DEFAULT TRUE,
    price_locked_at   TIMESTAMP,
    canceled_at       TIMESTAMP,
    cancel_reason     TEXT,
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE (tenant_id, module_id)
);

CREATE INDEX idx_tenant_modules_tenant ON tenant_modules(tenant_id);
CREATE INDEX idx_tenant_modules_status ON tenant_modules(status) WHERE status IN ('GRACE','SUSPENDED');
CREATE INDEX idx_tenant_modules_grace ON tenant_modules(grace_until) WHERE grace_until IS NOT NULL;
