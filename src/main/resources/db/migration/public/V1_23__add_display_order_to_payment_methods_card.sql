ALTER TABLE payment_methods_card ADD COLUMN display_order INT NOT NULL DEFAULT 0;

WITH ordered AS (
    SELECT id,
           ROW_NUMBER() OVER (
               PARTITION BY tenant_id
               ORDER BY is_default DESC, created_at ASC
           ) - 1 AS new_order
    FROM payment_methods_card
)
UPDATE payment_methods_card pmc
SET display_order = ordered.new_order
FROM ordered
WHERE pmc.id = ordered.id;
