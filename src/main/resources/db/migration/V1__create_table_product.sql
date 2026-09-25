CREATE TABLE product (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    category VARCHAR(50) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);