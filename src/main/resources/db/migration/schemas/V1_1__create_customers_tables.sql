CREATE TABLE customers (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    data        JSONB     NOT NULL DEFAULT '{}'::JSONB,
    deleted     BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_customers_name ON customers (((data ->> 'name'))) WHERE deleted = false;
CREATE UNIQUE INDEX idx_customers_cpf_unique
    ON customers ((data ->> 'cpf'))
    WHERE deleted = false AND data ->> 'cpf' IS NOT NULL;

CREATE TABLE customer_notes (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID      NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    content     TEXT      NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_customer_notes_customer ON customer_notes(customer_id);

CREATE TABLE customer_events (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID         NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    type        VARCHAR(50)  NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    date        TIMESTAMP    NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_customer_events_customer ON customer_events(customer_id);
CREATE INDEX idx_customer_events_date     ON customer_events(date);
