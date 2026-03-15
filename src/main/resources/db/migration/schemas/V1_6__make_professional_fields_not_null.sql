UPDATE professional_settings SET name = '' WHERE name IS NULL;
UPDATE professional_settings SET specialization = '' WHERE specialization IS NULL;

ALTER TABLE professional_settings ALTER COLUMN name SET NOT NULL, ALTER COLUMN name SET DEFAULT '';
ALTER TABLE professional_settings ALTER COLUMN specialization SET NOT NULL, ALTER COLUMN specialization SET DEFAULT '';

INSERT INTO professional_settings (name, crp, specialization, phone, email, contact_items)
SELECT 'Dra. Ana Silva', '06/12345', 'Psicologia Clínica', '(11) 98765-4321', 'ana.silva@clinica.com.br', '[]'::JSONB
WHERE NOT EXISTS (SELECT 1 FROM professional_settings);
