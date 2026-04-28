CREATE TABLE public.published_reports (
    id                  UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    report_id           UUID         NOT NULL,
    tenant_id           UUID         NOT NULL REFERENCES public.tenants(id) ON DELETE CASCADE,
    verification_code   VARCHAR(16)  NOT NULL UNIQUE,
    content_hash        VARCHAR(64)  NOT NULL,
    finalized_at        TIMESTAMP    NOT NULL,
    professional_name   VARCHAR(255),
    professional_crp    VARCHAR(50),
    customer_initials   VARCHAR(20),
    published_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_published_reports_code ON public.published_reports(verification_code);
CREATE UNIQUE INDEX uq_published_reports_tenant_report ON public.published_reports(tenant_id, report_id);
