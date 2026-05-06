ALTER TABLE public.email_log
    ADD COLUMN form_link_id UUID NULL;

UPDATE public.email_log
SET form_link_id = (tags->>'form_link_id')::uuid
WHERE tags->>'form_link_id' IS NOT NULL;

CREATE INDEX idx_email_log_form_link
    ON public.email_log(form_link_id, sent_at DESC)
    WHERE form_link_id IS NOT NULL;
