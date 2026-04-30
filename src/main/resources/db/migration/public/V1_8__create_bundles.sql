CREATE TABLE bundles (
    id                  VARCHAR(50)  PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    modules             JSONB        NOT NULL,
    price_monthly_cents INT          NOT NULL,
    price_yearly_cents  INT          NOT NULL,
    highlighted         BOOLEAN      NOT NULL DEFAULT FALSE,
    active              BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_bundles_active ON bundles(active) WHERE active = TRUE;

INSERT INTO bundles (id, name, modules, price_monthly_cents, price_yearly_cents, highlighted) VALUES
    ('solo',         'Solo',         '["reports","customers","calendar"]'::jsonb,                                14900,  143000, FALSE),
    ('profissional', 'Profissional', '["reports","customers","forms","calendar","ai_light"]'::jsonb,             29900,  287000, TRUE),
    ('clinica',      'Clínica',      '["reports","customers","forms","calendar","ai_pro","payments"]'::jsonb,    59900,  575000, FALSE);
