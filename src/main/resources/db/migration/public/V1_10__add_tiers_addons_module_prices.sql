ALTER TABLE bundles ADD COLUMN description             TEXT;
ALTER TABLE bundles ADD COLUMN seats_included          INT  NOT NULL DEFAULT 1;
ALTER TABLE bundles ADD COLUMN tracking_slots_included INT  NOT NULL DEFAULT 0;
ALTER TABLE bundles ADD COLUMN sort_order              INT  NOT NULL DEFAULT 0;

DELETE FROM bundles;

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

CREATE TABLE module_prices (
    id                  UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    module_id           VARCHAR(50)  NOT NULL,
    price_cents         INT          NOT NULL,
    currency            VARCHAR(3)   NOT NULL DEFAULT 'BRL',
    valid_from          TIMESTAMP    NOT NULL DEFAULT NOW(),
    valid_until         TIMESTAMP,
    notes               TEXT,
    created_by_user_id  UUID,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_module_prices_active ON module_prices(module_id) WHERE valid_until IS NULL;
CREATE INDEX idx_module_prices_history ON module_prices(module_id, valid_from DESC);

INSERT INTO module_prices (module_id, price_cents, notes) VALUES
    ('reports',    9900,  'Snapshot inicial'),
    ('customers',  5900,  'Snapshot inicial'),
    ('forms',      5900,  'Snapshot inicial'),
    ('calendar',   3900,  'Snapshot inicial'),
    ('ai_light',   9900,  'Snapshot inicial'),
    ('ai_pro',     19900, 'Snapshot inicial'),
    ('tracking',   9900,  'Snapshot inicial'),
    ('payments',   0,     'Snapshot inicial — fee 1.5%% via add-on'),
    ('financial',  2900,  'Snapshot inicial'),
    ('files_ocr',  2900,  'Snapshot inicial');

CREATE TABLE addons (
    id                     VARCHAR(50)   PRIMARY KEY,
    name                   VARCHAR(100)  NOT NULL,
    description            TEXT,
    type                   VARCHAR(20)   NOT NULL CHECK (type IN ('MODULE','SLOT_QUOTA','SEAT_QUOTA','PERCENTAGE_FEE')),
    target_module_id       VARCHAR(50),
    price_monthly_cents    INT           NOT NULL DEFAULT 0,
    price_unit_cents       INT,
    fee_percentage         DECIMAL(5,2),
    available_for_bundles  JSONB         NOT NULL DEFAULT '[]'::jsonb,
    active                 BOOLEAN       NOT NULL DEFAULT TRUE,
    sort_order             INT           NOT NULL DEFAULT 0,
    created_at             TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_addons_active ON addons(active) WHERE active = TRUE;

INSERT INTO addons (id, name, description, type, target_module_id, price_monthly_cents, price_unit_cents, fee_percentage, available_for_bundles, sort_order) VALUES
    ('payments_addon', 'DOX Pagamentos',
     'Receba pagamentos dos pacientes via PIX, Boleto ou Cartão diretamente na sua subconta',
     'PERCENTAGE_FEE', 'payments', 0, NULL, 1.5,
     '["essencial","plus","pro","clinica"]'::jsonb, 1),
    ('financial_addon', 'DOX Financeiro',
     'Dashboard financeiro com receitas, despesas, projeções e relatórios contábeis',
     'MODULE', 'financial', 2900, NULL, NULL,
     '["essencial","plus","pro","clinica"]'::jsonb, 2),
    ('files_ocr_addon', 'DOX Arquivos com OCR',
     'Anexe arquivos do paciente (PDF/imagens) com extração automática de texto via OCR',
     'MODULE', 'files_ocr', 2900, NULL, NULL,
     '["essencial","plus","pro","clinica"]'::jsonb, 3),
    ('extra_tracking_slot', 'Paciente adicional para Acompanhamento',
     'Mais um slot para acompanhar pacientes além dos inclusos no plano',
     'SLOT_QUOTA', 'tracking', 0, 299, NULL,
     '["pro","clinica"]'::jsonb, 4),
    ('extra_seat', 'Profissional adicional',
     'Mais um profissional na sua clínica',
     'SEAT_QUOTA', NULL, 0, 9900, NULL,
     '["clinica"]'::jsonb, 5);

CREATE TABLE tenant_addons (
    id                  UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID         NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    addon_id            VARCHAR(50)  NOT NULL REFERENCES addons(id),
    quantity            INT          NOT NULL DEFAULT 1,
    activated_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    canceled_at         TIMESTAMP,
    base_price_cents    INT          NOT NULL,
    final_price_cents   INT          NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE (tenant_id, addon_id)
);

CREATE INDEX idx_tenant_addons_tenant ON tenant_addons(tenant_id);
CREATE INDEX idx_tenant_addons_active ON tenant_addons(tenant_id, addon_id) WHERE canceled_at IS NULL;
