CREATE TABLE assessments (
    id                                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id                          UUID         NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    appointment_id                       UUID         REFERENCES calendar_events(id) ON DELETE SET NULL,
    applier_id                           UUID         NOT NULL,
    title                                VARCHAR(255) NOT NULL,
    category                             VARCHAR(100),
    applied_at                           DATE         NOT NULL,
    notes                                TEXT,
    parent_assessment_id                 UUID         REFERENCES assessments(id) ON DELETE SET NULL,
    professional_declaration_accepted_at TIMESTAMP    NOT NULL,
    professional_declaration_revision    INTEGER      NOT NULL DEFAULT 1,
    created_at                           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at                           TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted                              BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_assessments_customer    ON assessments(customer_id)             WHERE deleted = false;
CREATE INDEX idx_assessments_appointment ON assessments(appointment_id)          WHERE deleted = false;
CREATE INDEX idx_assessments_applied_at  ON assessments(applied_at DESC)         WHERE deleted = false;
CREATE INDEX idx_assessments_parent      ON assessments(parent_assessment_id)    WHERE deleted = false;

CREATE TABLE assessment_entries (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    assessment_id       UUID         NOT NULL REFERENCES assessments(id) ON DELETE CASCADE,
    instrument_name     VARCHAR(255) NOT NULL,
    entry_type          VARCHAR(20)  NOT NULL,
    order_index         INTEGER      NOT NULL DEFAULT 0,
    scores              JSONB        NOT NULL DEFAULT '[]'::JSONB,
    block               JSONB,
    observations        TEXT,
    attachment_file_id  UUID         REFERENCES customer_files(id) ON DELETE SET NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_entry_type CHECK (entry_type IN ('SIMPLE', 'TABLE', 'CHART')),
    CONSTRAINT chk_block_only_for_table_chart CHECK (
        (entry_type = 'SIMPLE' AND block IS NULL) OR
        (entry_type IN ('TABLE', 'CHART') AND block IS NOT NULL)
    )
);

CREATE INDEX idx_assessment_entries_assessment ON assessment_entries(assessment_id);
CREATE INDEX idx_assessment_entries_instrument ON assessment_entries(instrument_name);
