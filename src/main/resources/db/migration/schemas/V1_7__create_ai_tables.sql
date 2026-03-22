CREATE TABLE ai_quotas (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ai_tier         VARCHAR(20) NOT NULL DEFAULT 'NONE',
    model           VARCHAR(50) NOT NULL DEFAULT 'claude-sonnet-4-6',
    monthly_limit   INT NOT NULL DEFAULT 0,
    overage_price_cents INT NOT NULL DEFAULT 0,
    enabled         BOOLEAN NOT NULL DEFAULT false,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE ai_usages (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    report_id           UUID,
    generation_id       UUID NOT NULL DEFAULT gen_random_uuid(),
    professional_id     UUID NOT NULL,
    section_type        VARCHAR(50) NOT NULL,
    model               VARCHAR(50) NOT NULL,
    input_tokens        INT NOT NULL DEFAULT 0,
    output_tokens       INT NOT NULL DEFAULT 0,
    cache_read_tokens   INT NOT NULL DEFAULT 0,
    cache_write_tokens  INT NOT NULL DEFAULT 0,
    estimated_cost_brl  DECIMAL(10,4) NOT NULL DEFAULT 0,
    status              VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    error_message       TEXT,
    duration_ms         INT NOT NULL DEFAULT 0,
    is_regeneration     BOOLEAN NOT NULL DEFAULT false,
    regeneration_count  INT NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_ai_usages_professional_month ON ai_usages (professional_id, created_at);
CREATE INDEX idx_ai_usages_report ON ai_usages (report_id);
CREATE INDEX idx_ai_usages_generation ON ai_usages (generation_id);
