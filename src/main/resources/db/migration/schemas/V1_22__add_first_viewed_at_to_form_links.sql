ALTER TABLE form_links
    ADD COLUMN first_viewed_at TIMESTAMP NULL;

CREATE INDEX idx_form_links_first_viewed_at
    ON form_links(first_viewed_at)
    WHERE first_viewed_at IS NOT NULL;
