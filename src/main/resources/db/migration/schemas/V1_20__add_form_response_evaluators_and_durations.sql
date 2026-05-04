ALTER TABLE form_responses
    ADD COLUMN additional_evaluators JSONB NOT NULL DEFAULT '[]'::jsonb,
    ADD COLUMN page_durations_ms     JSONB NOT NULL DEFAULT '{}'::jsonb;
