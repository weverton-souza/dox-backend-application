ALTER TABLE reports
    ADD COLUMN finalized_at         TIMESTAMP NULL,
    ADD COLUMN content_hash         VARCHAR(64) NULL,
    ADD COLUMN finalized_by_user_id UUID NULL,
    ADD COLUMN finalized_by_ip      VARCHAR(45) NULL,
    ADD COLUMN finalized_user_agent VARCHAR(500) NULL;

CREATE OR REPLACE FUNCTION prevent_finalized_report_update()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.status = 'FINALIZADO' AND NEW.deleted = OLD.deleted THEN
        IF NEW.status            <> OLD.status
           OR NEW.blocks::text   <> OLD.blocks::text
           OR COALESCE(NEW.customer_name, '')              <> COALESCE(OLD.customer_name, '')
           OR COALESCE(NEW.customer_id::text, '')          <> COALESCE(OLD.customer_id::text, '')
           OR COALESCE(NEW.template_id::text, '')          <> COALESCE(OLD.template_id::text, '')
           OR COALESCE(NEW.form_response_id::text,'')      <> COALESCE(OLD.form_response_id::text, '')
           OR NEW.is_structure_locked                      <> OLD.is_structure_locked
           OR COALESCE(NEW.finalized_at::text, '')         <> COALESCE(OLD.finalized_at::text, '')
           OR COALESCE(NEW.content_hash, '')               <> COALESCE(OLD.content_hash, '')
           OR COALESCE(NEW.finalized_by_user_id::text, '') <> COALESCE(OLD.finalized_by_user_id::text, '')
           OR COALESCE(NEW.finalized_by_ip, '')            <> COALESCE(OLD.finalized_by_ip, '')
           OR COALESCE(NEW.finalized_user_agent, '')       <> COALESCE(OLD.finalized_user_agent, '')
        THEN
            RAISE EXCEPTION 'Relatório finalizado é imutável (id=%)', OLD.id
                USING ERRCODE = 'check_violation';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
