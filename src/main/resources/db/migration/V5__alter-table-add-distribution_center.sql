-- Adiciona o centro de distribuição de cada produto
ALTER TABLE product
ADD COLUMN distribution_center TEXT;

-- Distribui os produtos já existentes (inseridos na V4) de forma aleatória
-- entre os 3 centros de distribuição
UPDATE product
SET distribution_center = 'Mogi das Cruzes'
WHERE id IN ('p1', 'p6', 'p8', 'p10', 'p12', 'p16', 'p20');

UPDATE product
SET distribution_center = 'Recife'
WHERE id IN ('p2', 'p4', 'p7', 'p9', 'p13', 'p17', 'p19');

UPDATE product
SET distribution_center = 'Porto Alegre'
WHERE id IN ('p3', 'p5', 'p11', 'p14', 'p15', 'p18');

-- Garante que nenhum produto fique sem centro (ex.: produtos cadastrados
-- manualmente pelo POST antes desta migration rodar)
UPDATE product
SET distribution_center = 'Mogi das Cruzes'
WHERE distribution_center IS NULL;
