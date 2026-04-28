ALTER TABLE report_templates ADD COLUMN IF NOT EXISTS is_locked BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE report_templates ADD COLUMN IF NOT EXISTS is_master BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE reports
    ADD CONSTRAINT fk_reports_template
    FOREIGN KEY (template_id)
    REFERENCES report_templates(id)
    ON DELETE SET NULL;

