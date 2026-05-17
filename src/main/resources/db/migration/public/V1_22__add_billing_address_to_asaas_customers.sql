ALTER TABLE asaas_customers
    ADD COLUMN billing_mobile_phone   VARCHAR(20),
    ADD COLUMN billing_postal_code    VARCHAR(10),
    ADD COLUMN billing_address        VARCHAR(255),
    ADD COLUMN billing_address_number VARCHAR(20),
    ADD COLUMN billing_complement     VARCHAR(255),
    ADD COLUMN billing_province       VARCHAR(100);
