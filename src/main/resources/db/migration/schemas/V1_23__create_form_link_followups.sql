CREATE TABLE form_link_followups (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    form_link_id UUID NOT NULL REFERENCES form_links(id) ON DELETE CASCADE,
    level VARCHAR(20) NOT NULL CHECK (level IN ('SOFT', 'MEDIUM', 'URGENT')),
    day_offset INTEGER NOT NULL CHECK (day_offset > 0),
    scheduled_for TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' CHECK (status IN ('SCHEDULED', 'SENT', 'FAILED', 'SKIPPED')),
    email_log_id UUID NULL,
    error_message TEXT NULL,
    sent_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_form_link_followups_unique
    ON form_link_followups(form_link_id, level, day_offset);

CREATE INDEX idx_form_link_followups_due
    ON form_link_followups(scheduled_for, status)
    WHERE status = 'SCHEDULED';

CREATE INDEX idx_form_link_followups_link
    ON form_link_followups(form_link_id);
