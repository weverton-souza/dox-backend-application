CREATE TABLE report_templates (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    blocks      JSONB     NOT NULL DEFAULT '[]'::JSONB,
    is_default  BOOLEAN   NOT NULL DEFAULT FALSE,
    is_locked   BOOLEAN   NOT NULL DEFAULT FALSE,
    is_master   BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

ALTER TABLE reports
    ADD CONSTRAINT fk_reports_template
    FOREIGN KEY (template_id)
    REFERENCES report_templates(id)
    ON DELETE SET NULL;

CREATE TABLE score_table_templates (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    instrument_name VARCHAR(255),
    category        VARCHAR(255),
    columns         JSONB     NOT NULL DEFAULT '[]'::JSONB,
    rows            JSONB     NOT NULL DEFAULT '[]'::JSONB,
    footnote        JSONB,
    is_default      BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE chart_templates (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    instrument_name VARCHAR(255),
    category        VARCHAR(255),
    data            JSONB     NOT NULL DEFAULT '{}'::JSONB,
    is_default      BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
