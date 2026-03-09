CREATE TABLE reports (
    id                UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    status            VARCHAR(50) NOT NULL DEFAULT 'RASCUNHO',
    customer_name      VARCHAR(255),
    customer_id        UUID REFERENCES customers(id) ON DELETE SET NULL,
    form_response_id  UUID,
    blocks            JSONB NOT NULL DEFAULT '[]'::JSONB,
    deleted           BOOLEAN NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_reports_customer ON reports(customer_id) WHERE deleted = false;
CREATE INDEX idx_reports_status ON reports(status) WHERE deleted = false;

CREATE TABLE report_versions (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    report_id     UUID NOT NULL REFERENCES reports(id) ON DELETE CASCADE,
    status        VARCHAR(50) NOT NULL,
    description   VARCHAR(500),
    customer_name  VARCHAR(255),
    blocks        JSONB NOT NULL DEFAULT '[]'::JSONB,
    type          VARCHAR(50) NOT NULL DEFAULT 'manual',
    created_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_report_versions_report ON report_versions(report_id);
