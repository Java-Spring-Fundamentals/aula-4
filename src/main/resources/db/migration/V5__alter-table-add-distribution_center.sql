ALTER TABLE product
ADD COLUMN distribution_center TEXT;

UPDATE product
SET distribution_center = 'SP'
WHERE id IN ('p1', 'p2', 'p6', 'p9', 'p13', 'p17', 'p20');

UPDATE product
SET distribution_center = 'RJ'
WHERE id IN ('p3', 'p4', 'p8', 'p11', 'p15', 'p18');

UPDATE product
SET distribution_center = 'MG'
WHERE id IN ('p5', 'p7', 'p10', 'p12', 'p14', 'p16', 'p19');
