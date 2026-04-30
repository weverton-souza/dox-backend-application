CREATE TABLE asaas_customers (
    id                    UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id             UUID         NOT NULL UNIQUE REFERENCES tenants(id) ON DELETE CASCADE,
    asaas_customer_id     VARCHAR(255) NOT NULL UNIQUE,
    cpf_cnpj              VARCHAR(20)  NOT NULL,
    email                 VARCHAR(255),
    name                  VARCHAR(255) NOT NULL,
    created_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE subscriptions (
    id                       UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id                UUID         NOT NULL UNIQUE REFERENCES tenants(id) ON DELETE CASCADE,
    asaas_subscription_id    VARCHAR(255) UNIQUE,
    status                   VARCHAR(20)  NOT NULL CHECK (status IN ('TRIAL','TRIAL_GRACE','ACTIVE','GRACE','SUSPENDED','CANCEL_PENDING','CANCELED')),
    billing_cycle            VARCHAR(20)  NOT NULL CHECK (billing_cycle IN ('MONTHLY','QUARTERLY','SEMIANNUALLY','YEARLY')),
    billing_type             VARCHAR(20)  NOT NULL CHECK (billing_type IN ('BOLETO','PIX','CREDIT_CARD','UNDEFINED')),
    value_cents              INT          NOT NULL,
    current_period_start     TIMESTAMP,
    current_period_end       TIMESTAMP,
    next_due_date            DATE,
    trial_end                TIMESTAMP,
    canceled_at              TIMESTAMP,
    cancel_effective_at      TIMESTAMP,
    cancel_reason            TEXT,
    created_at               TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_subscriptions_status ON subscriptions(status) WHERE status IN ('GRACE','SUSPENDED','CANCEL_PENDING');
CREATE INDEX idx_subscriptions_cancel_effective ON subscriptions(cancel_effective_at) WHERE cancel_effective_at IS NOT NULL;

CREATE TABLE payments (
    id                  UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID         NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    subscription_id     UUID         REFERENCES subscriptions(id) ON DELETE SET NULL,
    asaas_payment_id    VARCHAR(255) NOT NULL UNIQUE,
    amount_cents        INT          NOT NULL,
    status              VARCHAR(30)  NOT NULL,
    billing_type        VARCHAR(20)  NOT NULL,
    due_date            DATE         NOT NULL,
    paid_at             TIMESTAMP,
    refunded_at         TIMESTAMP,
    invoice_url         TEXT,
    bank_slip_url       TEXT,
    pix_qr_code         TEXT,
    pix_copy_paste      TEXT,
    description         VARCHAR(500),
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payments_tenant ON payments(tenant_id);
CREATE INDEX idx_payments_subscription ON payments(subscription_id);
CREATE INDEX idx_payments_status ON payments(status);

CREATE TABLE payment_methods_card (
    id                          UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id                   UUID         NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    asaas_credit_card_token     VARCHAR(255) NOT NULL,
    brand                       VARCHAR(30)  NOT NULL,
    last4                       VARCHAR(4)   NOT NULL,
    holder_name                 VARCHAR(255) NOT NULL,
    is_default                  BOOLEAN      NOT NULL DEFAULT FALSE,
    expires_at                  DATE,
    created_at                  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payment_methods_card_tenant ON payment_methods_card(tenant_id);
CREATE UNIQUE INDEX uq_payment_methods_card_default ON payment_methods_card(tenant_id) WHERE is_default = TRUE;

CREATE TABLE invoices_nfse (
    id                  UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID         NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    payment_id          UUID         NOT NULL REFERENCES payments(id) ON DELETE CASCADE,
    asaas_invoice_id    VARCHAR(255) UNIQUE,
    status              VARCHAR(30)  NOT NULL,
    pdf_url             TEXT,
    xml_url             TEXT,
    error               TEXT,
    issued_at           TIMESTAMP,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_invoices_nfse_tenant ON invoices_nfse(tenant_id);
CREATE INDEX idx_invoices_nfse_payment ON invoices_nfse(payment_id);

CREATE TABLE processed_webhooks (
    id              UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    asaas_event_id  VARCHAR(255) NOT NULL UNIQUE,
    event_type      VARCHAR(50)  NOT NULL,
    payload         JSONB        NOT NULL,
    processed_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_processed_webhooks_event_type ON processed_webhooks(event_type);
