CREATE TABLE billing_audit_log (
    id              UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID         REFERENCES tenants(id) ON DELETE CASCADE,
    actor_admin_id  UUID         NOT NULL REFERENCES admin_users(id),
    action          VARCHAR(50)  NOT NULL
                                 CHECK (action IN (
                                     'GRANT_MODULE',
                                     'EXTEND_TRIAL',
                                     'LOCK_PRICE',
                                     'UNLOCK_PRICE',
                                     'EDIT_MODULE_PRICE',
                                     'EDIT_BUNDLE',
                                     'EDIT_ADDON'
                                 )),
    before_state    JSONB,
    after_state     JSONB,
    notes           TEXT,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_billing_audit_tenant ON billing_audit_log(tenant_id, created_at DESC);
CREATE INDEX idx_billing_audit_actor ON billing_audit_log(actor_admin_id, created_at DESC);
CREATE INDEX idx_billing_audit_catalog ON billing_audit_log(action, created_at DESC) WHERE tenant_id IS NULL;
