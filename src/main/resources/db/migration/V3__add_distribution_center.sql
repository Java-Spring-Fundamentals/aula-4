ALTER TABLE product ADD COLUMN distribution_center VARCHAR(50);

UPDATE product SET distribution_center = 'Mogi das Cruzes'
WHERE id IN ('p1', 'p5', 'p8');

UPDATE product SET distribution_center = 'Recife'
WHERE id IN ('p2', 'p4', 'p9', 'p10');

UPDATE product SET distribution_center = 'Porto Alegre'
WHERE id IN ('p3', 'p6', 'p7');

ALTER TABLE product ALTER COLUMN distribution_center SET NOT NULL;