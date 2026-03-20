CREATE TABLE professional_settings (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(255) NOT NULL DEFAULT '',
    crp             VARCHAR(50),
    specialization  VARCHAR(255) NOT NULL DEFAULT '',
    phone           VARCHAR(50),
    instagram       VARCHAR(255),
    email           VARCHAR(255),
    logo            TEXT,
    contact_items   JSONB     DEFAULT '[]'::JSONB,
    deleted         BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_professional_settings_crp_unique
    ON professional_settings (crp)
    WHERE deleted = false AND crp IS NOT NULL;
