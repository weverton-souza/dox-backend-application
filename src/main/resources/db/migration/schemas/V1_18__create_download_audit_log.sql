CREATE TABLE download_audit_log (
    id                   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    report_id            UUID         NOT NULL REFERENCES reports(id) ON DELETE CASCADE,
    user_id              UUID         NOT NULL,
    status_at_download   VARCHAR(50)  NOT NULL,
    ip_address           VARCHAR(45),
    user_agent           VARCHAR(500),
    downloaded_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_download_audit_log_report ON download_audit_log(report_id);
CREATE INDEX idx_download_audit_log_user   ON download_audit_log(user_id);
