CREATE TABLE ai_instructions (
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    type       VARCHAR(50)  NOT NULL,
    vertical   VARCHAR(50),
    content    TEXT         NOT NULL,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX uq_ai_instructions_type_vertical
    ON ai_instructions (type, COALESCE(vertical, '__NULL__'))
    WHERE active = TRUE;

CREATE INDEX idx_ai_instructions_type_active
    ON ai_instructions (type, active);
