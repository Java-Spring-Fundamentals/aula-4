ALTER TABLE product ADD COLUMN distribution_center VARCHAR(50);

UPDATE product SET distribution_center = 'Mogi das Cruzes' WHERE name IN ('Notebook Gamer X15', 'Cafeteira Expresso');
UPDATE product SET distribution_center = 'Recife' WHERE name IN ('Tenis de Corrida Runner', 'Cadeira Ergonomica');
UPDATE product SET distribution_center = 'Porto Alegre' WHERE name IN ('Fone Bluetooth ProSound', 'Smartwatch FitTrack');

ALTER TABLE product ALTER COLUMN distribution_center SET NOT NULL;
