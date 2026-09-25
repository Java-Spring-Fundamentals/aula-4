CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(100) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO product (name, category, price, active) VALUES
('Notebook Gamer X15', 'Eletronicos', 6499.90, true),
('Cafeteira Expresso', 'Eletrodomesticos', 899.00, true),
('Tenis de Corrida Runner', 'Calcados', 349.90, true),
('Cadeira Ergonomica', 'Moveis', 1299.00, true),
('Fone Bluetooth ProSound', 'Eletronicos', 279.90, true),
('Smartwatch FitTrack', 'Eletronicos', 599.00, true);
