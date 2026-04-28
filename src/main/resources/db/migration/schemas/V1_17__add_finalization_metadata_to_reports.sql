ALTER TABLE reports
    ADD COLUMN finalized_at TIMESTAMP NULL,
    ADD COLUMN content_hash VARCHAR(64) NULL;

CREATE OR REPLACE FUNCTION prevent_finalized_report_update()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.status = 'FINALIZADO' AND NEW.deleted = OLD.deleted THEN
        IF NEW.status            <> OLD.status
           OR NEW.blocks::text   <> OLD.blocks::text
           OR COALESCE(NEW.customer_name, '')         <> COALESCE(OLD.customer_name, '')
           OR COALESCE(NEW.customer_id::text, '')     <> COALESCE(OLD.customer_id::text, '')
           OR COALESCE(NEW.template_id::text, '')     <> COALESCE(OLD.template_id::text, '')
           OR COALESCE(NEW.form_response_id::text,'') <> COALESCE(OLD.form_response_id::text, '')
           OR NEW.is_structure_locked <> OLD.is_structure_locked
           OR COALESCE(NEW.finalized_at::text, '')    <> COALESCE(OLD.finalized_at::text, '')
           OR COALESCE(NEW.content_hash, '')          <> COALESCE(OLD.content_hash, '')
        THEN
            RAISE EXCEPTION 'Relatório finalizado é imutável (id=%)', OLD.id
                USING ERRCODE = 'check_violation';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
