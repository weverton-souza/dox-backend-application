CREATE TABLE ai_generation_sources (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    report_id UUID NOT NULL,
    generation_id UUID NOT NULL,
    source_type VARCHAR(30) NOT NULL,
    source_id UUID NOT NULL,
    source_label VARCHAR(255),
    included BOOLEAN NOT NULL DEFAULT true,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_ai_gen_sources_report ON ai_generation_sources(report_id);
CREATE INDEX idx_ai_gen_sources_generation ON ai_generation_sources(generation_id);

CREATE TABLE customer_files (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    file_name VARCHAR(500) NOT NULL,
    file_type VARCHAR(50),
    category VARCHAR(100),
    s3_key VARCHAR(1000),
    s3_url VARCHAR(2000),
    file_size_bytes BIGINT,
    uploaded_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    deleted BOOLEAN NOT NULL DEFAULT false
);

CREATE INDEX idx_customer_files_customer ON customer_files(customer_id) WHERE deleted = false;
