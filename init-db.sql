-- Create databases for both services
CREATE DATABASE order_db;
CREATE DATABASE inventory_db;

-- Connect to order_db and create tables
\c order_db;

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Connect to inventory_db and create tables
\c inventory_db;

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    category VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Insert sample products
INSERT INTO products (name, description, price, stock_quantity, category, created_at, updated_at) VALUES
('Laptop', 'High-performance laptop with latest specs', 999.99, 50, 'Electronics', NOW(), NOW()),
('Smartphone', 'Latest smartphone model', 699.99, 100, 'Electronics', NOW(), NOW()),
('Headphones', 'Wireless noise-cancelling headphones', 199.99, 75, 'Electronics', NOW(), NOW()),
('Book', 'Programming guide book', 29.99, 200, 'Books', NOW(), NOW()),
('Coffee Mug', 'Ceramic coffee mug', 9.99, 150, 'Home', NOW(), NOW());
