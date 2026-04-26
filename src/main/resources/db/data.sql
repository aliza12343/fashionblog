-- ============================================================
-- Fashion Blog – Seed Data
-- Run AFTER schema.sql:
--   mysql -u root -p fashion_db < src/main/resources/db/data.sql
--
-- All passwords are BCrypt hash of: password123
-- ============================================================

USE fashion_db;

INSERT INTO users (username, password, email, role) VALUES
('admin',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@fashionblog.com',    'ADMIN'),
('jane_doe',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'jane@fashionblog.com',     'USER'),
('fashion_fan', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'fanatic@fashionblog.com',  'USER');

INSERT INTO posts (title, content, image_url, price, category, stock_quantity, created_at, user_id) VALUES
('Summer Floral Dress',    'Light and breezy floral dress, perfect for summer outings.',  NULL, 49.99, 'Dresses',     25, NOW(), 1),
('Classic White Sneakers', 'Timeless white sneakers that pair with any outfit.',           NULL, 79.99, 'Footwear',    40, NOW(), 2),
('Oversized Denim Jacket', 'Relaxed-fit denim jacket, a wardrobe essential.',             NULL, 89.99, 'Outerwear',   15, NOW(), 2),
('Gold Hoop Earrings',     'Minimalist gold hoop earrings for everyday elegance.',        NULL, 24.99, 'Accessories', 60, NOW(), 3),
('High-Waist Linen Pants', 'Breathable linen trousers with a flattering high waist.',    NULL, 59.99, 'Bottoms',     20, NOW(), 3);
