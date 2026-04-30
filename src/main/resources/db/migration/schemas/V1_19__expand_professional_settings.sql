ALTER TABLE professional_settings
    ADD COLUMN gender          VARCHAR(20),
    ADD COLUMN social_name     VARCHAR(255),
    ADD COLUMN address_city    VARCHAR(100),
    ADD COLUMN address_state   VARCHAR(2),
    ADD COLUMN bio             VARCHAR(500),
    ADD COLUMN council_type    VARCHAR(20),
    ADD COLUMN council_number  VARCHAR(50),
    ADD COLUMN council_state   VARCHAR(2);

UPDATE professional_settings
SET council_type = 'CRP',
    council_number = crp
WHERE crp IS NOT NULL
  AND crp <> ''
  AND council_number IS NULL;
