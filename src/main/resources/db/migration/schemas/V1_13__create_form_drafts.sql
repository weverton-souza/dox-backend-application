CREATE TABLE form_drafts (
    form_link_id     UUID PRIMARY KEY REFERENCES form_links(id) ON DELETE CASCADE,
    partial_response JSONB     NOT NULL DEFAULT '{}'::jsonb,
    saved_at         TIMESTAMP NOT NULL DEFAULT NOW()
);
