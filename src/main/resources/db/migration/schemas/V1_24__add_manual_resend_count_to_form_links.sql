ALTER TABLE form_links
    ADD COLUMN manual_resend_count INTEGER NOT NULL DEFAULT 0
        CHECK (manual_resend_count >= 0 AND manual_resend_count <= 3);
