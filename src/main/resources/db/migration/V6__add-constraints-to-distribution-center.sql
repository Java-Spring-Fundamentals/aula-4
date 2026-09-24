ALTER TABLE product
ALTER COLUMN distribution_center SET NOT NULL;

ALTER TABLE product
ADD CONSTRAINT check_product_distribution_center
CHECK ( distribution_center IN ('Mogi das Cruzes', 'Recife', 'Porto Alegre'))