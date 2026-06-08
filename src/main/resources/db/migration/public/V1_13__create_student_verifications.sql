CREATE TABLE student_verifications (
    id                    UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id             UUID         NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    user_id               UUID         NOT NULL REFERENCES users(id),
    document_url          TEXT         NOT NULL,
    institution           VARCHAR(200),
    course                VARCHAR(200),
    expected_graduation   DATE,
    status                VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                                       CHECK (status IN ('PENDING','APPROVED','REJECTED')),
    reviewed_by_admin_id  UUID         REFERENCES admin_users(id),
    reviewed_at           TIMESTAMP,
    notes                 TEXT,
    rejection_reason      TEXT,
    created_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_student_verifications_status ON student_verifications(status, created_at DESC);
CREATE INDEX idx_student_verifications_tenant ON student_verifications(tenant_id);
