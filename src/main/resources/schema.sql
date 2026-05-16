-- E-Commerce Database Schema
-- MySQL 8.0+ compatible
-- Created for Spring Boot + JPA + Thymeleaf Application

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS ecommerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ecommerce_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_users_username (username),
    INDEX idx_users_email (email),
    INDEX idx_users_enabled (enabled),
    INDEX idx_users_role (role)
);

-- Products table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    image_url VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_products_name (name),
    INDEX idx_products_price (price),
    INDEX idx_products_stock (stock_quantity),
    INDEX idx_products_active (active)
);

-- Carts table
CREATE TABLE IF NOT EXISTS carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_carts_user_id (user_id),
    INDEX idx_carts_active (active)
);

-- Cart Items table
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY uk_cart_items_cart_product (cart_id, product_id),
    INDEX idx_cart_items_cart_id (cart_id),
    INDEX idx_cart_items_product_id (product_id)
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_price DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    shipping_address TEXT,
    billing_address TEXT,
    tracking_number VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_orders_user_id (user_id),
    INDEX idx_orders_status (status),
    INDEX idx_orders_order_date (order_date)
);

-- Order Items table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_order_items_order_id (order_id),
    INDEX idx_order_items_product_id (product_id)
);

-- Insert sample data for testing

-- Insert sample users
INSERT INTO users (username, email, password, first_name, last_name, role, enabled) VALUES
('admin', 'admin@ecommerce.com', '$2a$10$...bcrypt...', 'Admin', 'User', 'ADMIN', TRUE),
('john_doe', 'john@example.com', '$2a$10$...bcrypt...', 'John', 'Doe', 'USER', TRUE),
('jane_smith', 'jane@example.com', '$2a$10$...bcrypt...', 'Jane', 'Smith', 'USER', TRUE),
('mike_wilson', 'mike@example.com', '$2a$10$...bcrypt...', 'Mike', 'Wilson', 'USER', TRUE);

-- Insert sample products
INSERT INTO products (name, description, price, stock_quantity, image_url, active) VALUES
('Premium Laptop', 'High-performance laptop with latest specifications', 1299.99, 50, '/images/products/laptop.jpg', TRUE),
('Wireless Headphones', 'Premium noise-cancelling headphones', 199.99, 100, '/images/products/headphones.jpg', TRUE),
('Smart Watch', 'Feature-rich smartwatch with health tracking', 299.99, 75, '/images/products/watch.jpg', TRUE),
('Digital Camera', 'Professional DSLR camera for photography', 899.99, 30, '/images/products/camera.jpg', TRUE),
('Bluetooth Speaker', 'Portable Bluetooth speaker with excellent sound quality', 79.99, 150, '/images/products/speaker.jpg', TRUE),
('Gaming Mouse', 'Ergonomic gaming mouse with RGB lighting', 49.99, 200, '/images/products/mouse.jpg', TRUE),
('Mechanical Keyboard', 'Mechanical keyboard with customizable backlighting', 129.99, 80, '/images/products/keyboard.jpg', TRUE),
('USB-C Hub', 'Multi-port USB-C hub with fast charging', 39.99, 300, '/images/products/hub.jpg', TRUE),
('Phone Case', 'Durable phone case with screen protection', 19.99, 500, '/images/products/case.jpg', TRUE),
('Power Bank', '20000mAh portable power bank', 24.99, 400, '/images/products/powerbank.jpg', TRUE);

-- Insert sample carts (one per user)
INSERT INTO carts (user_id, active) VALUES
(2, TRUE), -- john_doe
(3, TRUE), -- jane_smith  
(4, TRUE); -- mike_wilson

-- Insert sample cart items
INSERT INTO cart_items (cart_id, product_id, quantity, unit_price) VALUES
(1, 1, 2, 1299.99), -- john_doe - Premium Laptop
(1, 2, 1, 199.99), -- john_doe - Wireless Headphones
(2, 1, 1, 299.99), -- jane_smith - Smart Watch
(3, 2, 1, 79.99), -- mike_wilson - Bluetooth Speaker
(4, 1, 1, 49.99); -- mike_wilson - Gaming Mouse

-- Insert sample orders
INSERT INTO orders (user_id, total_price, status, shipping_address, billing_address, tracking_number, notes) VALUES
(2, 1499.98, 'DELIVERED', '123 Main St, City, State 12345', '123 Main St, City, State 12345', 'TRK123456789', 'Delivered to front door'),
(3, 299.99, 'SHIPPED', '456 Oak Ave, Town, State 67890', '456 Oak Ave, Town, State 67890', 'TRK987654321', 'Shipped via express delivery');

-- Insert sample order items
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
(1, 1, 1, 1299.99), -- Order 1 - Premium Laptop
(1, 2, 1, 199.99), -- Order 1 - Wireless Headphones
(2, 1, 1, 299.99), -- Order 2 - Smart Watch
(2, 1, 1, 79.99); -- Order 2 - Bluetooth Speaker
