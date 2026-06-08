CREATE TABLE public.email_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NULL,
    template_id VARCHAR(64) NOT NULL,
    recipient_email VARCHAR(320) NOT NULL,
    subject VARCHAR(512) NOT NULL,
    provider_id VARCHAR(128) NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'SENT', 'FAILED', 'DELIVERED', 'BOUNCED', 'COMPLAINED', 'OPENED', 'CLICKED', 'SUPPRESSED')),
    error_message TEXT NULL,
    idempotency_key VARCHAR(256) NULL,
    form_link_id UUID NULL,
    tags JSONB NOT NULL DEFAULT '{}'::jsonb,
    sent_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_email_log_idempotency_key ON public.email_log(idempotency_key) WHERE idempotency_key IS NOT NULL;
CREATE INDEX idx_email_log_provider_id ON public.email_log(provider_id) WHERE provider_id IS NOT NULL;
CREATE INDEX idx_email_log_recipient ON public.email_log(recipient_email);
CREATE INDEX idx_email_log_template ON public.email_log(template_id, sent_at DESC);
CREATE INDEX idx_email_log_tenant ON public.email_log(tenant_id, sent_at DESC) WHERE tenant_id IS NOT NULL;
CREATE INDEX idx_email_log_status ON public.email_log(status, sent_at DESC);
CREATE INDEX idx_email_log_form_link ON public.email_log(form_link_id, sent_at DESC) WHERE form_link_id IS NOT NULL;
