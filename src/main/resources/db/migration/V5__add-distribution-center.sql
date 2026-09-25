ALTER TABLE product ADD distribution_center TEXT;

UPDATE product
SET distribution_center = CASE
    WHEN id IN (
        'dfc70678-865e-4505-9451-8ae8f49d5d61',
        'bd52ca2c-311b-4354-b2ea-cb5e46e3cc62',
        'a95f0b57-4a69-495e-9532-e98874b9fe2f',
        '795e935c-d12d-4666-b748-d2c91656ac14',
        '71c03a5b-73e5-43fc-8c60-bb1eeb7a3828',
        '703ea512-9a11-4c24-ad0a-49afd304f98b',
        '5c7f4f0a-b1b5-441c-8b71-684b6eb5118f'
    ) THEN 'Mogi das Cruzes'
    WHEN id IN (
        'a802ea0e-08d1-43f0-99e6-de3bd00ff82c',
        '4e6100d7-eee8-416f-ab4e-cc2d4086180c',
        '121969ae-822b-4995-a91c-d6f03cca74c6',
        'bca94e15-347b-4006-b38d-239c564b563f',
        '56f8aed2-b76f-4619-9120-23e826db990a',
        '907eef26-5fe6-4d44-b1a8-9777d2dfa8d7',
        '53690a36-aeb4-4df0-9d27-a9820253041c'
    ) THEN 'Recife'
    ELSE 'Porto Alegre'
END;

ALTER TABLE product ALTER COLUMN distribution_center SET NOT NULL;
