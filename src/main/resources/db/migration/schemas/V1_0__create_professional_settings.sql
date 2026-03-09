CREATE TABLE professional_settings (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(255),
    crp             VARCHAR(50),
    specialization  VARCHAR(255),
    phone           VARCHAR(50),
    instagram       VARCHAR(255),
    email           VARCHAR(255),
    logo            TEXT,
    contact_items   JSONB DEFAULT '[]'::JSONB,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
