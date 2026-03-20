CREATE TABLE forms (
    id                 UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    linked_template_id UUID REFERENCES report_templates(id) ON DELETE SET NULL,
    current_version    INT       NOT NULL DEFAULT 1,
    created_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE form_versions (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    form_id         UUID         NOT NULL REFERENCES forms(id) ON DELETE CASCADE,
    version         INT          NOT NULL DEFAULT 1,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    fields          JSONB     NOT NULL DEFAULT '[]'::JSONB,
    field_mappings  JSONB     DEFAULT '{}'::JSONB,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(form_id, version)
);

CREATE INDEX idx_form_versions_form ON form_versions(form_id);

CREATE TABLE form_responses (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    form_id             UUID         NOT NULL REFERENCES forms(id) ON DELETE CASCADE,
    form_version_id     UUID         NOT NULL REFERENCES form_versions(id) ON DELETE CASCADE,
    customer_id         UUID REFERENCES customers(id) ON DELETE SET NULL,
    customer_name       VARCHAR(255),
    status              VARCHAR(50)  NOT NULL DEFAULT 'EM_ANDAMENTO',
    answers             JSONB     NOT NULL DEFAULT '{}'::JSONB,
    generated_report_id UUID REFERENCES reports(id) ON DELETE SET NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_form_responses_form     ON form_responses(form_id);
CREATE INDEX idx_form_responses_version  ON form_responses(form_version_id);
CREATE INDEX idx_form_responses_customer ON form_responses(customer_id);

CREATE TABLE form_links (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    form_id     UUID        NOT NULL REFERENCES forms(id),
    customer_id UUID        NOT NULL REFERENCES customers(id),
    created_by  UUID        NOT NULL,
    status      VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    expires_at  TIMESTAMP   NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_form_links_status ON form_links(status);
