CREATE TABLE bundles (
    id                      VARCHAR(50)  PRIMARY KEY,
    name                    VARCHAR(100) NOT NULL,
    description             TEXT,
    modules                 JSONB        NOT NULL,
    price_monthly_cents     INT          NOT NULL,
    price_yearly_cents      INT          NOT NULL,
    seats_included          INT          NOT NULL DEFAULT 1,
    tracking_slots_included INT          NOT NULL DEFAULT 0,
    highlighted             BOOLEAN      NOT NULL DEFAULT FALSE,
    active                  BOOLEAN      NOT NULL DEFAULT TRUE,
    sort_order              INT          NOT NULL DEFAULT 0,
    created_at              TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_bundles_active ON bundles(active) WHERE active = TRUE;

INSERT INTO bundles (id, name, description, modules, price_monthly_cents, price_yearly_cents, seats_included, tracking_slots_included, highlighted, sort_order) VALUES
    ('essencial', 'Essencial', 'Para começar com o básico',
     '["reports","customers","forms","calendar"]'::jsonb,
     14990, 143900, 1, 0, FALSE, 1),
    ('plus', 'Plus', 'Essencial + DOX IA',
     '["reports","customers","forms","calendar","ai_light"]'::jsonb,
     24990, 239900, 1, 0, FALSE, 2),
    ('pro', 'Pro', 'Plus + Acompanhamento de até 3 pacientes',
     '["reports","customers","forms","calendar","ai_light","tracking"]'::jsonb,
     34990, 335900, 1, 3, TRUE, 3),
    ('clinica', 'Clínica', 'Pro com IA avançada e até 5 profissionais',
     '["reports","customers","forms","calendar","ai_pro","tracking","payments"]'::jsonb,
     59999, 575900, 3, 5, FALSE, 4);
