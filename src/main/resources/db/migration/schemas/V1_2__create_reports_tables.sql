CREATE TABLE reports (
    id                    UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    status                VARCHAR(50)  NOT NULL DEFAULT 'RASCUNHO',
    customer_name         VARCHAR(255),
    customer_id           UUID         REFERENCES customers(id) ON DELETE SET NULL,
    form_response_id      UUID,
    template_id           UUID,
    is_structure_locked   BOOLEAN      NOT NULL DEFAULT FALSE,
    blocks                JSONB        NOT NULL DEFAULT '[]'::JSONB,
    finalized_at          TIMESTAMP    NULL,
    content_hash          VARCHAR(64)  NULL,
    finalized_by_user_id  UUID         NULL,
    finalized_by_ip       VARCHAR(45)  NULL,
    finalized_user_agent  VARCHAR(500) NULL,
    version               BIGINT       NOT NULL DEFAULT 0,
    deleted               BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_reports_customer ON reports(customer_id) WHERE deleted = false;
CREATE INDEX idx_reports_status   ON reports(status)      WHERE deleted = false;

CREATE TABLE report_versions (
    id            UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    report_id     UUID         NOT NULL REFERENCES reports(id) ON DELETE CASCADE,
    status        VARCHAR(50)  NOT NULL,
    description   VARCHAR(500),
    customer_name VARCHAR(255),
    blocks        JSONB        NOT NULL DEFAULT '[]'::JSONB,
    type          VARCHAR(50)  NOT NULL DEFAULT 'manual',
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_report_versions_report ON report_versions(report_id);

CREATE OR REPLACE FUNCTION prevent_finalized_report_update()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.status = 'FINALIZADO' AND NEW.deleted = OLD.deleted THEN
        IF NEW.status            <> OLD.status
           OR NEW.blocks::text   <> OLD.blocks::text
           OR COALESCE(NEW.customer_name, '')              <> COALESCE(OLD.customer_name, '')
           OR COALESCE(NEW.customer_id::text, '')          <> COALESCE(OLD.customer_id::text, '')
           OR COALESCE(NEW.template_id::text, '')          <> COALESCE(OLD.template_id::text, '')
           OR COALESCE(NEW.form_response_id::text,'')      <> COALESCE(OLD.form_response_id::text, '')
           OR NEW.is_structure_locked                      <> OLD.is_structure_locked
           OR COALESCE(NEW.finalized_at::text, '')         <> COALESCE(OLD.finalized_at::text, '')
           OR COALESCE(NEW.content_hash, '')               <> COALESCE(OLD.content_hash, '')
           OR COALESCE(NEW.finalized_by_user_id::text, '') <> COALESCE(OLD.finalized_by_user_id::text, '')
           OR COALESCE(NEW.finalized_by_ip, '')            <> COALESCE(OLD.finalized_by_ip, '')
           OR COALESCE(NEW.finalized_user_agent, '')       <> COALESCE(OLD.finalized_user_agent, '')
        THEN
            RAISE EXCEPTION 'Relatório finalizado é imutável (id=%)', OLD.id
                USING ERRCODE = 'check_violation';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_prevent_finalized_report_update
    BEFORE UPDATE ON reports
    FOR EACH ROW
    EXECUTE FUNCTION prevent_finalized_report_update();
