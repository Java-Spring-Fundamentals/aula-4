-- o V5 usou ids p1, p2... que nao existem no seed, entao os produtos ficaram sem centro
UPDATE product
SET distribution_center = 'Mogi das Cruzes'
WHERE category = 'clothes';

UPDATE product
SET distribution_center = 'Recife'
WHERE category = 'electronics';

UPDATE product
SET distribution_center = 'Porto Alegre'
WHERE category = 'home';
